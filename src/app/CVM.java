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
import app.Components.Register;
import app.Components.URIClient;
import app.Components.URISensor;
import app.connectors.ConnectorSensor;
import fr.sorbonne_u.components.helpers.CVMDebugModes;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVM</code> implements the single JVM assembly for the client/sensornode
 *  example.
 *
 * <p><strong>Description</strong></p>
 * 
 * An URI provider component defined by the class <code>URIClient</code>
 * offers an URI creation service, which is used by an URI consumer component
 * defined by the class <code>URISensor</code>. Both are deployed within a
 * single JVM.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2014-01-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVM
extends		AbstractCVM
{
	/** URI of the provider component (convenience).						*/
	protected static final String	CLIENT_COMPONENT_URI = "my-URI-sensor";
	/** URI of the consumer component (convenience).						*/
	protected static final String	SENSORNODE_COMPONENT_URI = "my-URI-client";
	/** URI of the provider outbound port (simplifies the connection).		*/
	protected static final String	URIGetterOutboundPortURI = "oport";
	/** URI of the consumer inbound port (simplifies the connection).		*/
	protected static final String	URIClientInboundPortURI = "iport";

	
	protected static final String URIRegisterInboundPortURI = AbstractPort.generatePortURI();

	public				CVM() throws Exception
	{
		super() ;
	}

	/** Reference to the provider component to share between deploy
	 *  and shutdown.														*/
	protected String	URIClientURI;
	/** Reference to the consumer component to share between deploy
	 *  and shutdown.														*/
	protected String	uriSensorURI;
	
	protected String	RegisterURI;

	/**
	 * instantiate the components, publish their port and interconnect them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.deploymentDone()
	 * post	this.deploymentDone()
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
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

		
		// create the consumer component
				this.uriSensorURI =
					AbstractComponent.createComponent(
							Register.class.getCanonicalName(),
							new Object[]{URIRegisterInboundPortURI,});
				assert	this.isDeployedComponent(this.uriSensorURI);
				// make it trace its operations; comment and uncomment the line to see
				// the difference
				this.toggleTracing(this.uriSensorURI);
				this.toggleLogging(this.uriSensorURI);
				
		// create the client component
		this.URIClientURI =
			AbstractComponent.createComponent(
					URIClient.class.getCanonicalName(),
					new Object[]{URIRegisterInboundPortURI});
		assert	this.isDeployedComponent(this.URIClientURI);
		// make it trace its operations; comment and uncomment the line to see
		// the difference
		this.toggleTracing(this.URIClientURI);
		this.toggleLogging(this.URIClientURI);

//		// create the consumer component
//		this.uriSensorURI =
//			AbstractComponent.createComponent(
//					URISensor.class.getCanonicalName(),
//					new Object[]{SENSORNODE_COMPONENT_URI,
//								URIClientInboundPortURI});
//		assert	this.isDeployedComponent(this.uriSensorURI);
//		// make it trace its operations; comment and uncomment the line to see
//		// the difference
//		this.toggleTracing(this.uriSensorURI);
//		this.toggleLogging(this.uriSensorURI);
//		
	
		// Nota: the above use of the reference to the object representing
		// the URI consumer component is allowed only in the deployment
		// phase of the component virtual machine (to perform the static
		// interconnection of components in a static architecture) and
		// inside the concerned component (i.e., where the method
		// doPortConnection can be called with the this destination
		// (this.doPortConenction(...)). It must never be used in another
		// component as the references to objects used to implement component
		// features must not be shared among components.

		// ---------------------------------------------------------------------
		// Deployment done
		// ---------------------------------------------------------------------

		super.deploy();
		assert	this.deploymentDone();
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#finalise()
	 */
	@Override
	public void				finalise() throws Exception
	{
		// Port disconnections can be done here for static architectures
		// otherwise, they can be done in the finalise methods of components.
		

		super.finalise();
	}

	/**
	 * disconnect the components and then call the base shutdown method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void				shutdown() throws Exception
	{
		assert	this.allFinalised();
		// any disconnection not done yet can be performed here

		super.shutdown();
	}
	
	
	public static void main(String[] args)
	{
		try {
			// Create an instance of the defined component virtual machine.
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
