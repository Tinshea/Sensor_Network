package app.Models;

import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class Request implements RequestI {
    private static final long serialVersionUID = 1L;
    private final String uri;
    private ConnectionInfoI client;
    private QueryI queryCode; // Assurez-vous que Query implémente QueryI
 

    public Request(QueryI queryCode, ConnectionInfoI client) {
        this.queryCode = queryCode;
        this.client = client;
        this.uri = AbstractPort.generatePortURI();
    }

    @Override
    public String requestURI() {
        return uri;
    }

    @Override
    public QueryI getQueryCode() {
        return queryCode;
    }

    @Override
    public boolean isAsynchronous() {
        return false;
    }

    @Override
    public ConnectionInfoI clientConnectionInfo() {
        return null;
    }

}