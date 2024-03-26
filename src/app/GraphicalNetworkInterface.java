package app;

public interface GraphicalNetworkInterface {
    void addGraphicalNode(String name, int x, int y);
    void addGraphicalConnection(String startName, String endName);
    void removeGraphicalConnection(String startName, String endName);
    void startGraphicalLightAnimation(String startName, String endName);
	void toggleNodeBlinking(String nodeIdentifier);
}
