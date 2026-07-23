# Build and run the sensor network on Windows -- no Eclipse required.
#
#   .\run.ps1                            # mono-JVM scenario (app.CVM)
#   .\run.ps1 -Tests                     # compile and run the JUnit 5 suite
#   .\run.ps1 -Main app.DistributedCVM   # multi-JVM node (see README)
#   .\run.ps1 -SkipBuild                 # reuse the classes already in bin\
#   .\run.ps1 -NoAssertions              # run without -ea

param(
    [string]$Main = 'app.CVM',
    [switch]$Tests,
    [switch]$SkipBuild,
    [switch]$NoAssertions
)

$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$bin = Join-Path $root 'bin'
$testBin = Join-Path $root 'bin-test'
$junitVersion = '1.10.2'
$junitJar = Join-Path $root ".junit\junit-platform-console-standalone-$junitVersion.jar"

foreach ($tool in 'javac', 'java') {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
        throw "$tool not found on PATH. Install a JDK (8 or later) and try again."
    }
}

# CPS.jar is a prebuilt snapshot of this very project; leaving it off the
# classpath keeps it from shadowing what we just compiled into bin\.
$jars = Get-ChildItem (Join-Path $root 'deployment\jars\*.jar') |
        Where-Object { $_.Name -ne 'CPS.jar' } |
        ForEach-Object { $_.FullName }

# javac's @argfile must not carry a BOM, so bypass PowerShell's default encoding.
function Write-ArgFile($paths, $path) {
    [System.IO.File]::WriteAllLines($path, $paths, (New-Object System.Text.UTF8Encoding $false))
}

# src/tests needs JUnit on the classpath, so the main build leaves it out.
if (-not $SkipBuild) {
    New-Item -ItemType Directory -Force $bin | Out-Null
    $sources = Get-ChildItem (Join-Path $root 'src') -Recurse -Filter *.java |
               Where-Object { $_.FullName -notmatch '\\src\\tests\\' } |
               ForEach-Object { $_.FullName }
    $argfile = Join-Path $env:TEMP 'sensor_network_sources.txt'
    Write-ArgFile $sources $argfile

    javac -nowarn -encoding UTF-8 -d $bin -cp ($jars -join ';') "@$argfile"
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    Write-Host "Compiled $($sources.Count) source files into $bin"
}

if ($Tests) {
    if (-not (Test-Path $junitJar)) {
        $url = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$junitVersion/junit-platform-console-standalone-$junitVersion.jar"
        Write-Host "JUnit console launcher not found; downloading it from Maven Central:"
        Write-Host "  $url"
        New-Item -ItemType Directory -Force (Split-Path $junitJar) | Out-Null
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri $url -OutFile $junitJar -UseBasicParsing
    }

    New-Item -ItemType Directory -Force $testBin | Out-Null
    $testSources = Get-ChildItem (Join-Path $root 'src\tests') -Recurse -Filter *.java |
                   ForEach-Object { $_.FullName }
    $testArgfile = Join-Path $env:TEMP 'sensor_network_tests.txt'
    Write-ArgFile $testSources $testArgfile

    $testCp = (($jars + $bin + $junitJar) -join ';')
    javac -nowarn -encoding UTF-8 -d $testBin -cp $testCp "@$testArgfile"
    if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
    Write-Host "Compiled $($testSources.Count) test files into $testBin"

    & java -jar $junitJar execute `
        --class-path (($jars + $bin + $testBin) -join ';') `
        --scan-class-path $testBin --details=summary --disable-ansi-colors
    exit $LASTEXITCODE
}

$javaArgs = @()
if (-not $NoAssertions) { $javaArgs += '-ea' }   # BCM4Java checks its contracts with assertions
$javaArgs += @('-cp', (($jars + $bin) -join ';'), $Main)

Write-Host "Running $Main -- about 30s, opens ~55 trace windows. Ctrl+C to stop."
Set-Location $root
& java @javaArgs
