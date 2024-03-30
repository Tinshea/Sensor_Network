package app;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import app.Models.ClientConfig;
import app.Models.Position;
import app.Models.SensorConfig;
import app.Models.SensorData;
import app.config.Config;
import app.factory.ComponentFactory;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

public class CVM extends AbstractCVM
{
	
	// ------------------------------------------------------------------------
	// Instance variables
	// ------------------------------------------------------------------------
	protected static final String URIRegisterInboundPortURINode = AbstractPort.generatePortURI();
	protected static final String URIRegisterInboundPortURIClient = AbstractPort.generatePortURI();
	protected String serverClock;
    protected ArrayList<Position> positions;
    protected ArrayList<Set<SensorDataI>> nodeSensors;
    protected String registerURI;
    protected ArrayList<String> clientURIs;
    protected ArrayList<String> sensorURIs;
    private NetworkPanel panel;
	
	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	
	  public CVM() throws Exception {
	        super();
	        this.positions = new ArrayList<>();
	        this.nodeSensors = new ArrayList<>();
	        this.clientURIs = new ArrayList<>();
	        this.sensorURIs = new ArrayList<>();
	        initializePositionsAndSensors();
	    }
	  
	  private void initializePositionsAndSensors() {
	        // Dynamically generate positions based on Config.NBNODE and generate sensor data for each position

		  	initializePositions(Config.ROW, Config.COLUM);
	        initializeSensors();
	    }
												
	@Override
	public void deploy() throws Exception {
	    assert !this.deploymentDone();

	    configureDebugMode();
	    createAndShowGUI();
	    createServerClock();
	    createRegisterComponent();
	    createClientComponents();
	    createSensorComponents();

	    super.deploy();
	    assert this.deploymentDone();
	}


	@Override
	public void	 finalise() throws Exception
	{
		super.finalise();
	}

	@Override
	public void	 shutdown() throws Exception
	{
		assert	this.allFinalised();
		super.shutdown();
	}
	
	
	

	//--------------------------------------------------------------------------
	// services
	//--------------------------------------------------------------------------
	private void createServerClock() throws Exception {
	    long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + Config.START_DELAY);
	    this.serverClock = AbstractComponent.createComponent(
	        ClocksServer.class.getCanonicalName(),
	        new Object[]{Config.TEST_CLOCK_URI, unixEpochStartTimeInNanos, Instant.parse(Config.START_INSTANT), Config.ACCELERATION_FACTOR});
	}

	
	private void createAndShowGUI() {
	    SwingUtilities.invokeLater(() -> {
	        JFrame frame = new JFrame("Network Graph");
	        panel = new NetworkPanel();

	        // Définir la taille préférée du panneau en fonction de la taille de votre grille
	        panel.setPreferredSize(new Dimension(2000, 2000)); // Ajustez en fonction de la taille de votre réseau

	        // Ajouter le panneau à un JScrollPane
	        JScrollPane scrollPane = new JScrollPane(panel);
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

	        // Récupérer les dimensions de l'écran pour positionner et dimensionner le cadre
	        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	        int width = screenSize.width;
	        int height = screenSize.height;

	        // Positionner la fenêtre en haut à droite et définir la taille de la fenêtre
	        frame.setSize(width / 3, height);
	        frame.setLocation(width - frame.getWidth(), 0);

	        // Ajouter le JScrollPane au cadre au lieu du panneau directement
	        frame.add(scrollPane);
	        // Positionnez la barre de défilement verticale en bas
	        javax.swing.SwingUtilities.invokeLater(() -> {
	            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
	            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
	        });
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setVisible(true);
	    });
	}
	
	private Double generateSensorValue() {
	    // Générer une valeur aléatoire entre 0.0 et 100.0
	    double value = new Random().nextDouble() * 100;
	    // Arrondir à deux chiffres après la virgule
	    return Math.round(value * 100.0) / 100.0;
	}
	
	private void configureDebugMode() {
	    AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
	    AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
	    AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
	    AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
	    AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
	    AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);
	}
	
	private void createRegisterComponent() throws Exception {
	    this.registerURI = ComponentFactory.createRegister(URIRegisterInboundPortURINode, URIRegisterInboundPortURIClient);
	    this.toggleTracing(this.registerURI);
	    this.toggleLogging(this.registerURI);
	}


	private void createClientComponents() throws Exception {
		
	    for (int i = 0; i < Config.NBCLIENT; i++) {
	    	ClientConfig configClient = new ClientConfig(
					 URIRegisterInboundPortURIClient, 
			            Config.TEST_CLOCK_URI, 
			            i+1
			        );
	        String clientURI = ComponentFactory.createClient(configClient);
	        
	        this.toggleTracing(clientURI);
	        this.toggleLogging(clientURI);
	        clientURIs.add(clientURI);
	    }
	}

	private void createSensorComponents() throws Exception {
	    for (int i = 0; i < Config.NBNODE; i++) {
	        SensorConfig configNode = new SensorConfig(
	            this.panel, 
	            URIRegisterInboundPortURINode, 
	            Config.TEST_CLOCK_URI, 
	            nodeSensors.get(i), 
	            i+1, 
	            positions.get(i)
	        );
	        
	        String sensorURI = ComponentFactory.createSensor(configNode);
	        
	        this.toggleTracing(sensorURI);
	        this.toggleLogging(sensorURI);
	        sensorURIs.add(sensorURI);
	    }
	}
	
	private void initializeSensors() {
	    for (int i = 1; i <= Config.NBNODE; i++) {
	        Set<SensorDataI> sensorsForNode = new HashSet<>();

	        // Ajouter des capteurs avec des données générées pour chaque type
	        sensorsForNode.add(new SensorData("Node" + i, "Weather", generateSensorValue()));
	        sensorsForNode.add(new SensorData("Node" + i, "WindSpeed", generateSensorValue()));
	        sensorsForNode.add(new SensorData("Node" + i, "WindDirection", generateSensorValue()));
	        sensorsForNode.add(new SensorData("Node" + i, "Smoke", generateSensorValue()));
	        sensorsForNode.add(new SensorData("Node" + i, "Heat", generateSensorValue()));
	        sensorsForNode.add(new SensorData("Node" + i, "Biological", generateSensorValue()));

	        // Ajouter l'ensemble des capteurs pour ce nœud à l'ensemble principal
	        nodeSensors.add(sensorsForNode);
	    }
	}

	private void initializePositions(int rows, int cols) {
	    this.positions.clear(); // Nettoyer la liste au cas où elle a été précédemment utilisée.
	    
	    int nodeCount = 0; // Compteur pour les nœuds créés
	    // Itérer sur chaque ligne et colonne pour créer des nœuds
	    for (int row = 0; row < rows; row++) {
	        int yOffset = (row % 2 == 0) ? 0 : 1; // Décalage pour les lignes paires et impaires

	        for (int col = 0; col < cols - yOffset; col++) {
	            if (nodeCount < Config.NBNODE) {
	                int x = col * 2 + 1 + yOffset; // x commence à 1 et alterne entre les décalages de 0 et 1
	                int y = rows - row; // Inverser la coordonnée y pour commencer par le haut
	                positions.add(new Position(x, y));
	                nodeCount++;
	            }
	        }
	    }
	}

	
	public static void main(String[] args)
	{
		try {
			// Create an instance of the defined component virtual machine.
//			VerboseException.VERBOSE = true;
			CVM a = new CVM();
			// Execute the application.
			a.startStandardLifeCycle(20000L);
			// Give some time to see the traces (convenience).
			Thread.sleep(5000L);
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------