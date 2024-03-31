package AST.Rand;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public interface IRand {
      public double eval(ExecutionStateI curentNode);
}
