package app;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import app.Components.Register;
import app.Components.Client;
import app.Components.Sensor;
import app.Models.Bcm4javaEndPointDescriptor;
import app.Models.Descriptor;
import app.Models.Position;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

public class CVM extends AbstractCVM
{
	// ------------------------------------------------------------------------
	// Instance variables
	// ------------------------------------------------------------------------
	
	public static final String TEST_CLOCK_URI = "test-clock";
	public static final Instant START_INSTANT = Instant.parse("2024-03-18T20:05:00.00Z");
	protected static final long START_DELAY = 8000L;
	public static final double ACCELERATION_FACTOR = 60.0;
	long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
	protected String serverClock;
	
	protected ArrayList<Position> Positions  = new ArrayList<>();
	
	protected static final String URIRegisterInboundPortURI = AbstractPort.generatePortURI();
	protected String RegisterURI;
	
	protected ArrayList<String> URIClientURI = new ArrayList<>();
	protected static final int NBCLIENT = 1;
	
	protected ArrayList<String> uriSensorsURI = new ArrayList<>();
	protected static int NBNODE;
	
	private static NetworkPanel panel;
	
	
	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	
	public CVM() throws Exception
	{
		super() ;
		Positions.add(new Position(1, 5)); //n1
		Positions.add(new Position(3, 5)); //n2
		Positions.add(new Position(5, 5)); //n3
		
		Positions.add(new Position(2, 4)); //n4
		Positions.add(new Position(4, 4)); //n5
		
		Positions.add(new Position(1, 3)); //n6
		Positions.add(new Position(3, 3)); //n7
		Positions.add(new Position(5, 3)); //n8
		
		Positions.add(new Position(2, 2)); //n9
		Positions.add(new Position(4, 2)); //n10
		
		Positions.add(new Position(1, 1)); //n11
		Positions.add(new Position(3, 1)); //n12
		Positions.add(new Position(5, 1)); //n13
		
		NBNODE = Positions.size();
	}
												
	@Override
	public void	deploy() throws Exception
	{
		assert	!this.deploymentDone() ;

		// ---------------------------------------------------------------------
		// Configuration phase
		// ---------------------------------------------------------------------

		// debugging mode configuration; comment and uncomment the line to see
		// the difference
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);

		// ---------------------------------------------------------------------
		// Creation phase
		// ---------------------------------------------------------------------
		createAndShowGUI();
		//create the server clock component
		
		this.serverClock = AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{
				TEST_CLOCK_URI,// URI attribuée à l’horloge 
				unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
				START_INSTANT,// instant de démarrage du scénario
				ACCELERATION_FACTOR});// facteur d’acccélération
		
		// create the register component
		this.RegisterURI =
			AbstractComponent.createComponent(
					Register.class.getCanonicalName(),
					new Object[]{URIRegisterInboundPortURI,});
		
		assert this.isDeployedComponent(this.RegisterURI);

		this.toggleTracing(this.RegisterURI);
		this.toggleLogging(this.RegisterURI);
				
		// create the client component
		for (int i =0 ; i < NBCLIENT ; i++) {
			this.URIClientURI.add(
				AbstractComponent.createComponent(
						Client.class.getCanonicalName(),
						new Object[]{
								i + 1,
								URIRegisterInboundPortURI,
								TEST_CLOCK_URI,
								}));
			
			assert this.isDeployedComponent(this.URIClientURI.get(i));
	
			this.toggleTracing(this.URIClientURI.get(i));
			this.toggleLogging(this.URIClientURI.get(i));
		}

		// create the consumer component
		
		for (int i =0 ; i < NBNODE ; i++) {
			BCM4JavaEndPointDescriptorI urinode = new Bcm4javaEndPointDescriptor(AbstractPort.generatePortURI());
			
			NodeInfoI nodeDescription = new Descriptor("n"+(i+1),urinode,Positions.get(i),1.5,null);
			
			this.uriSensorsURI.add(	AbstractComponent.createComponent(
						Sensor.class.getCanonicalName(),
						new Object[]{
								this.panel,
								nodeDescription, 
								URIRegisterInboundPortURI,
								TEST_CLOCK_URI,
								}));
			
			assert	this.isDeployedComponent(uriSensorsURI.get(i));
	
			this.toggleTracing(uriSensorsURI.get(i));
			this.toggleLogging(uriSensorsURI.get(i));
		}

		// ---------------------------------------------------------------------
		// Deployment done
		// ---------------------------------------------------------------------

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
		// any disconnection not done yet can be performed here

		super.shutdown();
	}
	
	private void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Network Graph");
            panel = new NetworkPanel(); // Initialisez 'panel' ici

            frame.add(panel);
            frame.setSize(600, 700);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double width = screenSize.getWidth();
            double height = screenSize.getHeight();
            
            // Positionner la fenêtre en haut à droite
            frame.setLocation((int)width - frame.getWidth(), 0);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            
        });
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
