package app.Components;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import app.Interfaces.RegisterCI;
import app.Ports.URIRegisterInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

@OfferedInterfaces(offered = {LookupCI.class, RegistrationCI.class, RegisterCI.class})


public class Register  extends AbstractComponent {
	
	protected final URIRegisterInboundPort inboundPort ;
	
	private Set<NodeInfoI> registeredNodes = new HashSet<>();
	//identité, position, portée de ses émetteurs et informations de connexion dans le cas de ce projet
	protected Register(String outboundPortURI) throws Exception {
		
		super(1, 0) ;
		
		this.inboundPort = new URIRegisterInboundPort(outboundPortURI ,this) ;
		this.inboundPort.localPublishPort() ;
		
	}
	
	//-------------------------------------------------------------------------
	// Component life-cycle
	//-------------------------------------------------------------------------
	
	@Override
	public void start() throws ComponentStartException {
		this.logMessage("starting register component.") ;
		super.start() ;
	}
	
	@Override
	public void finalise() throws Exception {
		this.logMessage("stopping Register component.") ;
		this.printExecutionLogOnFile("Register");
		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		super.shutdownNow();
	}

	//-------------------------------------------------------------------------
	// Component internal services
	//-------------------------------------------------------------------------
		public boolean registered(String nodeIdentifier)throws Exception{
			return registeredNodes.stream()
	                .anyMatch(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
		}
		
		public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
			// Enregistre un nouveau nœud et retourne 
			//Au plus quatre voisins lui seront attribués, un au
			//plus pour chaque direction (nord-ouest, nord-est, sud-ouest et sud-est) à condition qu’il en existe
			//et que le nouveau nœud et le voisin soient dans leur portée d’émission mutuelle
			
			this.logMessage("adding nodeInfo : " + nodeInfo.nodeIdentifier()) ;
		    registeredNodes.add(nodeInfo);
		    Position p = (Position) nodeInfo.nodePosition();
		    Set<NodeInfoI> neighbours = new HashSet<>();

		    NodeInfoI closestNorthEastNeighbour = null;
		    NodeInfoI closestNorthWestNeighbour = null;
		    NodeInfoI closestSouthEastNeighbour = null;
		    NodeInfoI closestSouthWestNeighbour = null;
		    
		    double closestNE = Double.MAX_VALUE;
		    double closestNW = Double.MAX_VALUE;
		    double closestSE = Double.MAX_VALUE;
		    double closestSW = Double.MAX_VALUE;

		    for (NodeInfoI otherNodeInfo : registeredNodes) {
		        if (!otherNodeInfo.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
		            Position otherPosition = (Position) otherNodeInfo.nodePosition();
		            double distance = p.distance(otherPosition);
		            if (distance <= nodeInfo.nodeRange() && distance <= otherNodeInfo.nodeRange()) {
		                if (p.northOf(otherPosition) && p.eastOf(otherPosition) && distance < closestNE) {
		                    closestNE = distance;
		                    closestNorthEastNeighbour = otherNodeInfo;
		                } else if (p.northOf(otherPosition) && p.westOf(otherPosition) && distance < closestNW) {
		                    closestNW = distance;
		                    closestNorthWestNeighbour = otherNodeInfo;
		                } else if (p.southOf(otherPosition) && p.eastOf(otherPosition) && distance < closestSE) {
		                    closestSE = distance;
		                    closestSouthEastNeighbour = otherNodeInfo;
		                } else if (p.southOf(otherPosition) && p.westOf(otherPosition) && distance < closestSW) {
		                    closestSW = distance;
		                    closestSouthWestNeighbour = otherNodeInfo;
		                }
		            }
		        }
		    }

		    if (closestNorthEastNeighbour != null) neighbours.add(closestNorthEastNeighbour);
		    if (closestNorthWestNeighbour != null) neighbours.add(closestNorthWestNeighbour);
		    if (closestSouthEastNeighbour != null) neighbours.add(closestSouthEastNeighbour);
		    if (closestSouthWestNeighbour != null) neighbours.add(closestSouthWestNeighbour);

		    return neighbours;
		}

		
		public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction direction) throws Exception {
			this.logMessage("Request received") ;
		    Position position = (Position) nodeInfo.nodePosition();
		    NodeInfoI closestNeighbour = null;
		    double closestDistance = Double.MAX_VALUE;

		    for (NodeInfoI potentialNeighbour : registeredNodes) {
		        if (!potentialNeighbour.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
		            Position potentialPosition = (Position) potentialNeighbour.nodePosition();
		            double distance = position.distance(potentialPosition);
		            Direction potentialDirection = position.directionFrom(potentialPosition);

		            if (distance <= nodeInfo.nodeRange() && distance <= potentialNeighbour.nodeRange() && potentialDirection == direction) {
		                if (distance < closestDistance) {
		                    closestDistance = distance;
		                    closestNeighbour = potentialNeighbour;
		                }
		            }
		        }
		    }

		    return closestNeighbour;
		}

		public void unregister(String nodeIdentifier) throws Exception{
			// Désenregistre un nœud
	        registeredNodes.removeIf(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
		}
		
		public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		    return registeredNodes.stream()
		            .filter(nodeInfo -> nodeInfo.nodeIdentifier().equals(sensorNodeId))
		            .findFirst()
		            .orElse(null); // Retourne null si le nœud n'est pas trouvé
		}

		public Set<ConnectionInfoI> findByZone(GeographicalZoneI zone) throws Exception {
		    // Filtrer les nœuds enregistrés basés sur le critère que leur position est dans la zone spécifiée
		    return registeredNodes.stream()
		            .filter(nodeInfo -> zone.in(nodeInfo.nodePosition()))
		            .collect(Collectors.toSet());
		}


}
