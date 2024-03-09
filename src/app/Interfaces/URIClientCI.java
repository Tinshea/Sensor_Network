package app.Interfaces;

import java.util.Set;

//Copyright Jacques Malenfant, Sorbonne Universite.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability.
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or
//data to be ensured and,  more generally, to use and operate it in the
//same conditions as regards security.
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

//-----------------------------------------------------------------------------
/**
 * The interface <code>URIConsumerCI</code> defines the interface required by a
 * component that needs to get URI from an URI provider component.
 *
 * <p><strong>Description</strong></p>
 *
 * As a RMI remote interface, all of the methods must return
 * <code>RemoteException</code>. The choice here is to throw
 * <code>Exception</code> to cater for potential exceptions
 * thrown by the implementation methods.
 *
 * <p>Created on : 2014-01-22</p>
 *
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface URIClientCI extends RequiredCI, LookupCI, RequestingCI {

	
	public QueryResultI execute(RequestI request) throws Exception ;

	public void executeAsync(RequestI request) throws Exception;

	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception ;
	
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception ;
	
}
//-----------------------------------------------------------------------------
