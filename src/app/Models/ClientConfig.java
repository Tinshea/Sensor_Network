package app.Models;

public class ClientConfig {
    private final String inboundPortRegister;
    private final String uriClock;
    private final int name;


    public ClientConfig(String inboundPortRegister, String uriClock, int name) {
        this.inboundPortRegister = inboundPortRegister;
        this.uriClock = uriClock;
        this.name = name;
    }

    public String getInboundPortRegister() {
        return inboundPortRegister;
    }

    public String getUriClock() {
        return uriClock;
    }

    public int getName() {
        return name;
    }
}


