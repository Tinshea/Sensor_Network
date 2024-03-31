package app;

public interface GraphicalNetworkInterface {
    void addGraphicalNode(String name, double x, double y);
    void addGraphicalConnection(String startName, String endName);
    void removeGraphicalConnection(String startName, String endName);
    void startGraphicalLightAnimation(String startName, String endName);
	void toggleNodeBlinking(String nodeIdentifier);
}
