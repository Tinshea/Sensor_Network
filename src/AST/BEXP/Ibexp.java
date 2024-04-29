package AST.BEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface Ibexp {
    public boolean eval(ExecutionStateI curentNode);
}
