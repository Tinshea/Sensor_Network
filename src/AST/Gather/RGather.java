package AST.Gather;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class RGather implements IGather , Serializable {
    private static final long serialVersionUID = 19L;

    private String sensorID;
    private IGather gather;
    
    public RGather(String sensorID,IGather gather) {
        this.sensorID = sensorID;
        this.gather = gather;
    }
    
    @Override
    public String getSensorID() {
        // TODO Auto-generated method stub
        return this.sensorID;
    }
    
    public IGather getGather() {
        return this.gather;
    }
    
    @Override
    public List<SensorDataI> eval(ExecutionStateI es) {
        // TODO Auto-generated method stub
        ArrayList<SensorDataI> v = new ArrayList<>();
        v.add(es.getProcessingNode().getSensorData(sensorID));
        v.addAll(gather.eval(es));
        return v;
    }

}