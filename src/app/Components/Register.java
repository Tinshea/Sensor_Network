package app.Components;

import java.util.HashSet;
import java.util.Set;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

@OfferedInterfaces(offered = {LookupCI.class, RegistrationCI.class})

public class Register  extends AbstractComponent {
	
	private Set<NodeInfoI> registeredNodes = new HashSet<>();
	//identité, position, portée de ses émetteurs et informations de connexion dans le cas de ce projet
	protected Register() {
		
		super(1, 0) ;
	}
		public boolean registered(String nodeIdentifier)throws Exception{
			return registeredNodes.stream()
	                .anyMatch(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
			
		}
		
		public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
			// Enregistre un nouveau nœud et retourne 
			//Au plus quatre voisins lui seront attribués, un au
			//plus pour chaque direction (nord-ouest, nord-est, sud-ouest et sud-est) à condition qu’il en existe
			//et que le nouveau nœud et le voisin soient dans leur portée d’émission mutuelle
			
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

		
		public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo) throws Exception{
		     // Trouve un nouveau voisin pour le nœud donné
	        // Cette implémentation est simplifiée, elle retourne simplement le premier voisin qui n'est pas le nœud lui-même
	        return registeredNodes.stream()
	                .filter(neighbour -> !neighbour.nodeIdentifier().equals(nodeInfo.nodeIdentifier()))
	                .findFirst()
	                .orElse(null); // Retourne null si aucun voisin n'est trouvé
	    }

		public void unregister(String nodeIdentifier) throws Exception{
			// Désenregistre un nœud
	        registeredNodes.removeIf(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
		}
		
		public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception{
			return null;
			
		}
		public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception{
			return null;
			
		}

}
