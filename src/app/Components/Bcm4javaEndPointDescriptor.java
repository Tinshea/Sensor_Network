package app.Components;

import java.util.HashSet;
import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;

public class Bcm4javaEndPointDescriptor implements BCM4JavaEndPointDescriptorI {

	/**
	 * 
	 */
	  private static final long serialVersionUID = 1L;
	    private String inboundPortURI;
	    private Set<Class<? extends OfferedCI>> offeredInterfaces;

	    public Bcm4javaEndPointDescriptor(String inboundPortURI) {
	        this.inboundPortURI = inboundPortURI;
	        this.offeredInterfaces = new HashSet<>();
	    }

	    @Override
	    public String getInboundPortURI() {
	        return this.inboundPortURI;
	    }

	    @Override
	    public boolean isOfferedInterface(Class<? extends OfferedCI> inter) {
	        return this.offeredInterfaces.contains(inter);
	    }

}
