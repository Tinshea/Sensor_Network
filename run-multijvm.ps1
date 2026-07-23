# Bring up the whole multi-JVM deployment on Windows: cyclic barrier, global
# registry, then the six JVMs described in deployment/config.xml.
#
#   .\run-multijvm.ps1              # build, launch everything, wait, clean up
#   .\run-multijvm.ps1 -SkipBuild
#   .\run-multijvm.ps1 -KeepRunning # do not kill survivors on exit
#
# Per-JVM output lands in logs/multijvm/. See the README for what this
# deployment does and does not currently do.

param(
    [switch]$SkipBuild,
    [switch]$KeepRunning,
    [int]$TimeoutSeconds = 100,
    [int]$Jvm0HeadStartSeconds = 10
)

$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$bin = Join-Path $root 'bin'
$dep = Join-Path $root 'deployment'
$logs = Join-Path $root 'logs\multijvm'

if (-not $SkipBuild) {
    $sources = Get-ChildItem (Join-Path $root 'src') -Recurse -Filter *.java |
               Where-Object { $_.FullName -notmatch '\\src\\tests\\' } |
               ForEach-Object { $_.FullName }
    $argfile = Join-Path $env:TEMP 'sensor_network_sources.txt'
    # javac's @argfile must not carry a BOM, so bypass PowerShell's default encoding.
    [System.IO.File]::WriteAllLines($argfile, $sources, (New-Object System.Text.UTF8Encoding $false))
    New-Item -ItemType Directory -Force $bin | Out-Null
    $buildCp = (Get-ChildItem (Join-Path $dep 'jars\*.jar') |
                Where-Object { $_.Name -ne 'CPS.jar' } |
                ForEach-Object { $_.FullName }) -join ';'
    javac -nowarn -encoding UTF-8 -d $bin -cp $buildCp "@$argfile"
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    Write-Host "Compiled $($sources.Count) source files into $bin"
}

New-Item -ItemType Directory -Force $logs | Out-Null
# A JVM from a previous run may still hold a handle; stale logs are harmless.
Get-ChildItem $logs -Filter *.log -ErrorAction SilentlyContinue |
    Remove-Item -Force -ErrorAction SilentlyContinue

# CPS.jar is a prebuilt snapshot of this project; keep it off the classpath so
# the freshly compiled classes in bin\ are what actually run.
$jars = Get-ChildItem (Join-Path $dep 'jars\*.jar') |
        Where-Object { $_.Name -ne 'CPS.jar' } |
        ForEach-Object { $_.FullName }
$cp = (($jars + $bin) -join ';')

# Everything runs from deployment/, where config.xml and dcvm.policy live.
$common = @('-ea', '-cp', $cp, '-Djava.security.policy=dcvm.policy')
$procs = @{}

function Start-Node([string]$name, [string[]]$tail) {
    $p = Start-Process java -ArgumentList ($common + $tail) -WorkingDirectory $dep `
        -RedirectStandardOutput (Join-Path $logs "$name.log") `
        -RedirectStandardError (Join-Path $logs "$name.err.log") `
        -PassThru -NoNewWindow
    Write-Host ("  {0,-9} pid {1}" -f $name, $p.Id)
    $script:procs[$name] = $p
}

Write-Host 'Starting support services...'
Start-Node 'barrier'  @('fr.sorbonne_u.components.cvm.utils.DCVMCyclicBarrier', 'config.xml')
Start-Node 'registry' @('fr.sorbonne_u.components.registry.GlobalRegistry', 'config.xml')
Start-Sleep -Seconds 5

# jvm0 publishes the clock and the registry into the global registry while it is
# still in instantiateAndPublish. jvm1-5 connect to those ports during their own
# instantiateAndPublish, i.e. in the same barrier phase, so the barrier does not
# order the two. Give jvm0 a head start or the others fail with "not bound!".
Write-Host 'Starting jvm0 (clock + registry)...'
Start-Node 'jvm0' @('app.DistributedCVM', 'jvm0', 'config.xml')
Start-Sleep -Seconds $Jvm0HeadStartSeconds

Write-Host 'Starting jvm1-5 (1 client + 10 nodes each)...'
foreach ($j in 1..5) {
    Start-Node "jvm$j" @('app.DistributedCVM', "jvm$j", 'config.xml')
    Start-Sleep -Milliseconds 400
}

Write-Host "Waiting up to ${TimeoutSeconds}s for the scenario to finish..."
$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
while ((Get-Date) -lt $deadline) {
    if (-not ($procs.Values | Where-Object { -not $_.HasExited })) { break }
    Start-Sleep -Seconds 2
}

if (-not $KeepRunning) {
    foreach ($p in $procs.Values) {
        if (-not $p.HasExited) { try { $p.Kill() } catch {} }
    }
}

Write-Host "`nOutcome (stderr bytes > 0 means that JVM reported a problem):"
foreach ($name in @('barrier', 'registry') + (0..5 | ForEach-Object { "jvm$_" })) {
    $err = Join-Path $logs "$name.err.log"
    $size = if (Test-Path $err) { (Get-Item $err).Length } else { 0 }
    Write-Host ("  {0,-9} exit={1,-6} stderr={2}" -f $name, $procs[$name].ExitCode, $size)
}
Write-Host "`nLogs in $logs; query results in deployment\client.log"
