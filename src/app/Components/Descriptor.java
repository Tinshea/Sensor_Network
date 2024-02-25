package app.Components;

import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

public class Descriptor implements NodeInfoI {

	private static final long serialVersionUID = 1L;

	@Override
	public String nodeIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EndPointDescriptorI endPointInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PositionI nodePosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double nodeRange() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EndPointDescriptorI p2pEndPointInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
