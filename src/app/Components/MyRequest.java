package app.Components;

import Interfaces.IGather;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class MyRequest implements RequestI {
    private static final long serialVersionUID = 1L;
	private QueryI uri;
    //private Query queryCode; // Assurez-vous que Query implémente QueryI
 

    public MyRequest(QueryI uri) {
        this.uri = uri;
    }

	@Override
	public String requestURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryI getQueryCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsynchronous() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ConnectionInfoI clientConnectionInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryI getUri() {
		return uri;
	}

	public void setUri(QueryI uri) {
		this.uri = uri;
	}

}
