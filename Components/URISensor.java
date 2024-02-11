
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
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.examples.basic_cs.interfaces.URIProviderCI;
import fr.sorbonne_u.components.examples.basic_cs.ports.URIProviderInboundPort;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>URISensor</code> implements a component that provides
 * URI creation services.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		this.uriPrefix != null
 * invariant		c.isOfferedInterface(URIProviderI.class)
 * </pre>
 * 
 * <p>Created on : 2014-01-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered = {URISensorCI.class})
public class			URISensor
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constructors and instance variables
	// -------------------------------------------------------------------------

	/**	a string prefix that will identify the URI Sensor.				*/
	protected String		uriPrefix;

	/**
	 * check the invariant of the class on an instance.
	 *
	 * @param c	the component to be tested.
	 */
	protected static void	checkInvariant(URIProvider c)
	{
		assert	c.uriPrefix != null :
					new InvariantException("The URI prefix is null!");
		assert	c.isOfferedInterface(URISensorCI.class) :
					new InvariantException("The URI component should "
							+ "offer the interface URISensorCI!");
	}

	/**
	 * create a component with a given uri prefix and that will expose its
	 * service through a port of the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uriPrefix != null and providerPortURI != null
	 * post	this.uriPrefix.equals(uriPrefix)
	 * post	this.isPortExisting(providerPortURI)
	 * post	this.findPortFromURI(providerPortURI).getImplementedInterface().equals(URIProviderI.class)
	 * post	this.findPortFromURI(providerPortURI).isPublished()
	 * </pre>
	 *
	 * @param uriPrefix			the URI prefix of this provider.
	 * @param providerPortURI	the URI of the port exposing the service.
	 * @throws Exception			<i>todo.</i>
	 */
	protected				URISensor(
		String uriPrefix,
		String sensorPortURI
		) throws Exception
	{
		// the reflection inbound port URI is the URI of the component
		super(uriPrefix, 1, 0) ;

		assert	uriPrefix != null :
					new PreconditionException("uri can't be null!");
		assert	sensorPortURI != null :
					new PreconditionException("providerPortURI can't be null!");

		this.uriPrefix = uriPrefix ;

		// if the offered interface is not declared in an annotation on
		// the component class, it can be added manually with the
		// following instruction:
		//this.addOfferedInterface(URIProviderI.class) ;

		// create the port that exposes the offered interface with the
		// given URI to ease the connection from client components.
		PortI p = new URISensorCIInboundPort(sensorPortURI, this); 
		// publish the port
		p.publishPort();

		if (AbstractCVM.isDistributed) {
			this.getLogger().setDirectory(System.getProperty("user.dir"));
		} else {
			this.getLogger().setDirectory(System.getProperty("user.home"));
		}
		this.getTracer().setTitle("provider");
		this.getTracer().setRelativePosition(1, 0);

		//faire ça
		URISensor.checkInvariant(this) ;
		AbstractComponent.checkImplementationInvariant(this);
		AbstractComponent.checkInvariant(this);
		assert	this.uriPrefix.equals(uriPrefix) :
					new PostconditionException("The URI prefix has not "
												+ "been initialised!");
		assert	this.isPortExisting(providerPortURI) :
					new PostconditionException("The component must have a "
							+ "port with URI " + providerPortURI);
		assert	this.findPortFromURI(sensorPortURI).
					getImplementedInterface().equals(URISensorCI.class) :
					new PostconditionException("The component must have a "
							+ "port with implemented interface URIProviderI");
		assert	this.findPortFromURI(providerPortURI).isPublished() :
					new PostconditionException("The component must have a "
							+ "port published with URI " + providerPortURI);
	}

	//--------------------------------------------------------------------------
	// Component life-cycle
	//--------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		this.logMessage("starting sensor component.");
		super.start();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping sensor component.");
		this.printExecutionLogOnFile("sensor");
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] p = this.findPortsFromInterface(URISensorCI.class);
			p[0].unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			PortI[] p = this.findPortsFromInterface(URISensorCI.class);
			p[0].unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}

	//--------------------------------------------------------------------------
	// Component internal services
	//--------------------------------------------------------------------------

public QueryResultI executeSensorService(QueryI query) {
	
	ExecutionStateI
	return query.eval(//provide ExecutionState);
}
// -----------------------------------------------------------------------------