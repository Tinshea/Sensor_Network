package app;
import app.Models.*;
import app.factory.RequestFactory;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import app.config.Config;
import app.factory.ComponentFactory;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import app.gui.NetworkPanel;

import static app.factory.RequestFactory.createRequest;

/**
 * La classe CVM gere l'initialisation et le deploiement des composants de
 * simulation pour un reseau de capteurs, y compris les horloges, les interfaces
 * utilisateur et la connexion des composants clients et capteurs. Elle herite
 * de {@link AbstractCVM} et configure les differents aspects du cycle de vie
 * des composants, la visualisation, et la synchronisation des horloges.
 *
 * @author Malek Bouzarkouna, Younes Chetouani, Amine Zemali
 */
public class CVM extends AbstractCVM {
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

	/**
	 * Constructeur pour la classe CVM qui initialise les configurations des
	 * capteurs et des positions.
	 * 
	 * @throws Exception si une erreur se produit lors de l'initialisation.
	 */
	public CVM() throws Exception {
		super();
		this.positions = new ArrayList<>();
		this.nodeSensors = new ArrayList<>();
		this.clientURIs = new ArrayList<>();
		this.sensorURIs = new ArrayList<>();
		initializePositionsAndSensors();
	}

	/**
	 * Deploie les composants necessaires et configure l'environnement pour la
	 * simulation.
	 * 
	 * @throws Exception si une erreur se produit pendant le deploiement.
	 */
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

	/**
	 * Finalise les composants avant leur arret.
	 * 
	 * @throws Exception si une erreur se produit pendant la finalisation.
	 */
	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

	/**
	 * Arrete les composants et libere les ressources.
	 * 
	 * @throws Exception si une erreur se produit pendant l'arret.
	 */
	@Override
	public void shutdown() throws Exception {
		assert this.allFinalised();
		super.shutdown();
	}

	// --------------------------------------------------------------------------
	// services
	// --------------------------------------------------------------------------

	/**
	 * Crée l'horloge de serveur pour synchroniser tous les composants du système.
	 * Utilise la configuration définie pour initier l'horloge à un moment
	 * spécifique et avec un facteur d'accélération.
	 * 
	 * @throws Exception si le composant d'horloge ne peut pas être créé ou
	 *                   configuré correctement.
	 */

	private void createServerClock() throws Exception {
		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + Config.START_DELAY);
		this.serverClock = AbstractComponent.createComponent(ClocksServer.class.getCanonicalName(),
				new Object[] { Config.TEST_CLOCK_URI, unixEpochStartTimeInNanos, Instant.parse(Config.START_INSTANT),
						Config.ACCELERATION_FACTOR });
	}

	/**
	 * Initialise et affiche l'interface graphique pour visualiser le reseau de
	 * capteurs. Configure un cadre et un panneau pour afficher des informations en
	 * temps reel sur le réseau.
	 */

	private void createAndShowGUI() {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Network Graph");
			panel = new NetworkPanel();

			panel.setPreferredSize(new Dimension(2000, 10000));

			JScrollPane scrollPane = new JScrollPane(panel);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = screenSize.width;
			int height = screenSize.height;

			frame.setSize(width / 3, height);
			frame.setLocation(width - frame.getWidth(), 0);

			frame.add(scrollPane);

			javax.swing.SwingUtilities.invokeLater(() -> {
				JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
				verticalScrollBar.setValue(verticalScrollBar.getMaximum());
			});
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}

	/**
	 * Génere une valeur aléatoire entre 0.0 et 100.0 pour similer les valeurs des
	 * capteurs
	 *
	 */
	private Double generateSensorValue() {
		double value = new Random().nextDouble() * 100;
		return Math.round(value * 100.0) / 100.0;
	}

	/**
	 * Configure les modes de debogage pour le CVM, permettant le suivi de
	 * differentes activites.
	 */
	private void configureDebugMode() {
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);
	}

	/**
	 * Création du Register
	 */
	private void createRegisterComponent() throws Exception {
		this.registerURI = ComponentFactory.createRegister(URIRegisterInboundPortURINode,
				URIRegisterInboundPortURIClient);
		this.toggleTracing(this.registerURI);
		this.toggleLogging(this.registerURI);
	}

	/**
	 * Création du Client
	 */

	private void createClientComponents() throws Exception {
        // Define a list of predefined request sets
        List<List<RequestI>> predefinedRequestSets = new ArrayList<>();

        // You can add more predefined sets as needed
        predefinedRequestSets.add(Arrays.asList(
                createRequest(RequestFactory.RequestType.SIMPLE, EnumSensorIdentifier.Heat),
                createRequest(RequestFactory.RequestType.FCONT_BASED, new Position(2, 1), 3, EnumSensorIdentifier.Heat),
                createRequest(RequestFactory.RequestType.CUSTOM_COMPLEX_QUERY, EnumSensorIdentifier.Heat,
                        EnumSensorIdentifier.WindDirection, new Position(2, 2), 3 ),
                createRequest(RequestFactory.RequestType.GQUERY_COMPLEX, 3, new Position(2, 1),
                        EnumSensorIdentifier.Heat, EnumSensorIdentifier.Heat, EnumSensorIdentifier.Heat),
                createRequest(RequestFactory.RequestType.VERIF_TEMP_SEUIL,20.0,EnumSensorIdentifier.Smoke,new Position(2,2),3)
        ));
        
        predefinedRequestSets.add(Arrays.asList(
        		 createRequest(RequestFactory.RequestType.GQUERY_COMPLEX, 3, new Position(2, 1),
                         EnumSensorIdentifier.Smoke, EnumSensorIdentifier.Heat, EnumSensorIdentifier.Biological),
                createRequest(RequestFactory.RequestType.BQUERY_COMPLEX, Direction.NE, 50.0, 3.0, 2,
                        EnumSensorIdentifier.Heat, EnumSensorIdentifier.WindDirection),
				createRequest(RequestFactory.RequestType.BQUERY_OR, 10.0, 20.0, new Position(2, 1), 3 , EnumSensorIdentifier.Humidity,EnumSensorIdentifier.Biological),
                createRequest(RequestFactory.RequestType.FCONT_WITH_MULTIPLE_SENSORS, new Position(1, 5),3, EnumSensorIdentifier.Weather, EnumSensorIdentifier.WindSpeed),
                createRequest(RequestFactory.RequestType.ECONT_WITH_RGATHER, EnumSensorIdentifier.Weather, EnumSensorIdentifier.WindSpeed)



        ));
        predefinedRequestSets.add(Arrays.asList(
                createRequest(RequestFactory.RequestType.BQUERY_OR, 30.0, 70.0, new Position(2, 1), 3 , EnumSensorIdentifier.WindSpeed,EnumSensorIdentifier.Heat),
                createRequest(RequestFactory.RequestType.DCONT, Direction.SE, 3, EnumSensorIdentifier.Heat),
                createRequest(RequestFactory.RequestType.DCONT, Direction.NE, 5, EnumSensorIdentifier.Heat),
                createRequest(RequestFactory.RequestType.VERIF_TEMP_SEUIL,80.0,EnumSensorIdentifier.Heat,new Position(2,2),3),
                createRequest(RequestFactory.RequestType.VERIF_TEMP_SEUIL,60.0,EnumSensorIdentifier.WindSpeed,new Position(2,2),3)

        ));
        predefinedRequestSets.add(Arrays.asList(

            createRequest(RequestFactory.RequestType.BQUERY_AND_VERGLAS,new Position(3,3),4),
                createRequest(RequestFactory.RequestType.BQUERY_AND_NOT_VERGLAS,new Position(3,3),4),
                createRequest(RequestFactory.RequestType.BQUERY_VERIFY_HUMIDITY,new Position(3,3),4,40.0)


        ));

		// Create clients and assign requests in a cyclic manner from the predefined
		// sets
//		 Random random = new Random();
		for (int i = 0; i < Config.NBCLIENT; i++) {
			// Select request set from predefined lists cycling through them
			List<RequestI> requests = predefinedRequestSets.get(i % predefinedRequestSets.size());

			ClientConfig configClient = new ClientConfig(URIRegisterInboundPortURIClient, Config.TEST_CLOCK_URI, i + 1,
					requests, "n5", Config.ASYNC);
//					+(1+random.nextInt(Config.NBNODE))

			String clientURI = ComponentFactory.createClient(configClient);
			this.toggleTracing(clientURI);
			this.toggleLogging(clientURI);
			clientURIs.add(clientURI);
		}
	}

	/**
	 * Création du Sensor
	 */
	private void createSensorComponents() throws Exception {
		for (int i = 0; i < Config.NBNODE; i++) {
			SensorConfig configNode = new SensorConfig(this.panel, URIRegisterInboundPortURINode, Config.TEST_CLOCK_URI,
					nodeSensors.get(i), i + 1, positions.get(i), 10);

			String sensorURI = ComponentFactory.createSensor(configNode);

			this.toggleTracing(sensorURI);
			this.toggleLogging(sensorURI);
			sensorURIs.add(sensorURI);
		}
	}

	/**
	 * Initialise et configure l'ensembles de données de capteurs pour chaque nœud
	 * dans le réseau de capteurs.
	 */
	private void initializeSensors() {
		for (int i = 1; i <= Config.NBNODE; i++) {

			Set<SensorDataI> sensorsForNode = new HashSet<>();
			sensorsForNode.add(new SensorData("Node" + i, EnumSensorIdentifier.Weather.name(), generateSensorValue()));
			sensorsForNode
					.add(new SensorData("Node" + i, EnumSensorIdentifier.WindSpeed.name(), generateSensorValue()));
			sensorsForNode
					.add(new SensorData("Node" + i, EnumSensorIdentifier.WindDirection.name(), generateSensorValue()));
			sensorsForNode.add(new SensorData("Node" + i, EnumSensorIdentifier.Smoke.name(), generateSensorValue()));
			sensorsForNode.add(new SensorData("Node" + i, EnumSensorIdentifier.Heat.name(), generateSensorValue()));
			sensorsForNode
					.add(new SensorData("Node" + i, EnumSensorIdentifier.Biological.name(), generateSensorValue()));
			sensorsForNode
			.add(new SensorData("Node" + i, EnumSensorIdentifier.Humidity.name(), generateSensorValue()));

			nodeSensors.add(sensorsForNode);
		}
	}

	/**
	 * Initialise et remplit la liste des positions des nœuds en utilisant une
	 * grille définie par les constantes configurées dans Config.ROW et
	 * Config.COLUM. Cette méthode cree une distribution spatiale des nœuds dans une
	 * grille qui peut avoir un décalage alterné sur chaque ligne, pour simuler par
	 * exemple un arrangement hexagonal ou une structure similaire. La méthode prend
	 * en compte un nombre maximal de nœuds spécifié par Config.NBNODE pour limiter
	 * le nombre de positions generees.
	 *
	 * La grille est remplie ligne par ligne. Chaque ligne peut avoir un décalage
	 * initial qui affecte la position x de chaque nœud sur cette ligne. Le nombre
	 * de colonnes effectif par ligne est reduit par ce decalage. Les coordonnees y
	 * sont calculees de maniere e commencer du haut de la grille, inversant ainsi
	 * les indices de ligne pour correspondre a une orientation visuelle
	 * traditionnelle.
	 */
	private void initializePositions() {
		this.positions.clear();
		int nodeCount = 0;
		int row = 1;

		// Continue creating rows until all nodes are placed
		while (nodeCount < Config.NBNODE) {
			int yOffset = (row % 2 == 0) ? 0 : 1; // Offset for even and odd rows
			for (int col = 0; col < Config.COLUM - yOffset; col++) {
				if (nodeCount >= Config.NBNODE) {
					break; // Stop if all nodes are placed
				}
				int x = col * 2 + 1 + yOffset; // X starts at 1 and alternates offset by row
				int y = row;
				positions.add(new Position(x, y));
				nodeCount++;
			}
			row++; // Move to the next row after finishing one row
		}
	}

	/**
	 * Initialise les positions et les donnees de capteurs basees sur la
	 * configuration.
	 */
	private void initializePositionsAndSensors() {
		initializePositions();
		initializeSensors();
	}

	/**
	 * Point d'entree principal pour demarrer la machine virtuelle de composants, il
	 * cree une instance de CVM.
	 * 
	 * @param args Arguments de ligne de commande (non utilises).
	 */
	public static void main(String[] args) {
		try {
			CVM a = new CVM();
			a.startStandardLifeCycle(200000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}