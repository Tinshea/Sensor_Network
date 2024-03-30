package app.factory;

import fr.sorbonne_u.components.AbstractComponent;
import app.Components.Client;
import app.Components.Register;
import app.Components.Sensor;
import app.Models.ClientConfig;
import app.Models.SensorConfig;

public class ComponentFactory {

    public static String createRegister(String uriInboundPortNode, String uriInboundPortClient) throws Exception {
        return AbstractComponent.createComponent(
            Register.class.getCanonicalName(),
            new Object[]{uriInboundPortNode, uriInboundPortClient}
        );
    }

    public static String createClient(ClientConfig config) throws Exception {
        return AbstractComponent.createComponent(
            Client.class.getCanonicalName(),
            new Object[]{config}
        );
    }

    public static String createSensor(SensorConfig config) throws Exception {
        return AbstractComponent.createComponent(
            Sensor.class.getCanonicalName(),
            new Object[]{config}
        );
    }
}
