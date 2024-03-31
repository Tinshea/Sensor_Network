package AST.BEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public interface Ibexp {
    public boolean eval(ExecutionStateI curentNode);
}
