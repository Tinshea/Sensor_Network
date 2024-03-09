package app.Models;

import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class Descriptor implements NodeInfoI {

    private static final long serialVersionUID = 1L;
    
    private String nodeIdentifier;
    private EndPointDescriptorI endPointInfo;
    private PositionI nodePosition;
    private double nodeRange;
    private EndPointDescriptorI p2pEndPointInfo;

    public Descriptor(String nodeIdentifier, EndPointDescriptorI endPointInfo, PositionI nodePosition, double nodeRange, EndPointDescriptorI p2pEndPointInfo) {
        this.nodeIdentifier = nodeIdentifier;
        this.endPointInfo = endPointInfo;
        this.nodePosition = nodePosition;
        this.nodeRange = nodeRange;
        this.p2pEndPointInfo = p2pEndPointInfo;
        
    }

    @Override
    public String nodeIdentifier() {
        return nodeIdentifier;
    }

    @Override
    public EndPointDescriptorI endPointInfo() {
        return endPointInfo ;
    }

    @Override
    public PositionI nodePosition() {
        return nodePosition;
    }

    @Override
    public double nodeRange() {
        return nodeRange;
    }

    @Override
    public EndPointDescriptorI p2pEndPointInfo() {
        return p2pEndPointInfo;
    }

}
