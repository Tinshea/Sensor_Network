package AST.Gather;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class FGather implements IGather , Serializable {
    private static final long serialVersionUID = 18L;
    private String sensorID;
    
    public FGather(String sensorID) {
        this.sensorID = sensorID;
    }
    
    @Override
    public String getSensorID() {
        return this.sensorID;
    }
    
    @Override
    public List<SensorDataI> eval(ExecutionStateI es) {
        ArrayList<SensorDataI> v = new ArrayList<>();
        v.add(es.getProcessingNode().getSensorData(this.sensorID));
        return v;
    }

}