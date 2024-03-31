package AST.Gather;

import java.util.ArrayList;
import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class FGather implements IGather {
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