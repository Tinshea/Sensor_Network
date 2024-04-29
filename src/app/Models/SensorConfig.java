package app.Models;

import java.util.Set;
import app.gui.GraphicalNetworkInterface;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

/**
 * Represents the configuration details for a sensor in a sensor network.
 * This class encapsulates all necessary information needed to initialize and manage a sensor,
 * including its connection to the network, graphical interface settings, and sensor-specific data.
 */
public class SensorConfig {
    private final GraphicalNetworkInterface gui;             // The graphical interface associated with the sensor
    private final String inboundPortRegister;                // The inbound port on the registry for this sensor
    private final String uriClock;                           // The URI of the clock server for time synchronization
    private final Set<SensorDataI> sensors;                  // A set of data points that this sensor will monitor
    private final int name;                                  // An identifier for the sensor
    private final Position position;                         // The physical or logical position of the sensor
	private final int nbthread;

    /**
     * Constructs a SensorConfig with specific settings for a sensor.
     *
     * @param gui The graphical interface to be used with the sensor.
     * @param inboundPortRegister The inbound port identifier for registering this sensor.
     * @param uriClock The URI for the clock server used for time synchronization.
     * @param sensors The set of sensor data interfaces this sensor will handle.
     * @param name The numerical identifier for the sensor.
     * @param position The position of the sensor, encapsulated in a {@link Position} object.
     */
    public SensorConfig(GraphicalNetworkInterface gui, String inboundPortRegister, String uriClock, 
                        Set<SensorDataI> sensors, int name, Position position, int nbthread) {
        this.gui = gui;
        this.inboundPortRegister = inboundPortRegister;
        this.uriClock = uriClock;
        this.sensors = sensors;
        this.name = name;
        this.position = position;
        this.nbthread = nbthread;
    }

    /**
     * Retrieves the graphical interface associated with this sensor.
     * @return The graphical interface for managing sensor visualization.
     */
    public GraphicalNetworkInterface getGui() {
        return gui;
    }

    /**
     * Retrieves the inbound port used for registering the sensor.
     * @return The inbound port identifier as a string.
     */
    public String getInboundPortRegister() {
        return inboundPortRegister;
    }

    /**
     * Retrieves the URI of the clock server associated with this sensor.
     * @return The clock server URI as a string.
     */
    public String getUriClock() {
        return uriClock;
    }

    /**
     * Retrieves the set of sensor data that this sensor will monitor.
     * @return A set of {@link SensorDataI}, representing the data points the sensor handles.
     */
    public Set<SensorDataI> getSensors() {
        return sensors;
    }

    /**
     * Retrieves the identifier of the sensor.
     * @return The sensor's identifier as an integer.
     */
    public int getName() {
        return name;
    }

    /**
     * Retrieves the position of the sensor.
     * @return The position of the sensor, represented by a {@link Position} object.
     */
    public Position getPosition() {
        return position;
    }

	public int getNbthread() {
		// TODO Auto-generated method stub
		return this.nbthread;
	}
}
