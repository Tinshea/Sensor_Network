package app.Models;

import java.util.Set;

import app.GraphicalNetworkInterface;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class SensorConfig {
    private final GraphicalNetworkInterface gui;
    private final String inboundPortRegister;
    private final String uriClock;
    private final Set<SensorDataI> sensors;
    private final int name;
    private final Position position;

    public SensorConfig(GraphicalNetworkInterface gui, String inboundPortRegister, String uriClock, Set<SensorDataI> sensors, int name, Position position) {
        this.gui = gui;
        this.inboundPortRegister = inboundPortRegister;
        this.uriClock = uriClock;
        this.sensors = sensors;
        this.name = name;
        this.position = position;
    }

    public GraphicalNetworkInterface getGui() {
        return gui;
    }

    public String getInboundPortRegister() {
        return inboundPortRegister;
    }

    public String getUriClock() {
        return uriClock;
    }

    public Set<SensorDataI> getSensors() {
        return sensors;
    }

    public int getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }
}

