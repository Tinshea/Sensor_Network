package AST.CEXP;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import AST.Rand.IRand;

import java.io.Serializable;


/**
 * The class EqCExp must be used to evaluate the equality between two Icexp, it return true if they are equal.
 */
public class EqCExp implements Icexp , Serializable {
	private static final long serialVersionUID = 8L;

	private IRand rand1;
	private IRand rand2;
	
	 public EqCExp(IRand rand1, IRand rand2) {
		    this.rand1 = rand1;
		    this.rand2 = rand2;
		  }
		   @Override
    public boolean eval(ExecutionStateI es) {
        return rand1.eval(es) == rand2.eval(es);
    }

}
