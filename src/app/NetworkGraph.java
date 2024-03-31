package app;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Classe pour représenter un nœud
class Node {
    String name;
    int x, y;
    static final int DIAMETER = 50;
    boolean isBlinking = false; // Champ pour gérer le clignotement

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    // Méthode pour basculer l'état de clignotement
    public void toggleBlinking() {
        isBlinking = !isBlinking;
    }

    // Méthode draw mise à jour pour gérer le clignotement en rouge
    public void draw(Graphics g, boolean isOn) {
        g.setColor(Color.BLACK); // Couleur noire pour le contour
        g.fillOval(x, y, DIAMETER, DIAMETER); // Dessiner un cercle plein pour le contour

        // Si le nœud clignote et que isOn est vrai, dessiner l'intérieur en rouge
        if (isBlinking && isOn) {
            g.setColor(Color.RED); // Couleur rouge pour le clignotement
        } else {
            g.setColor(Color.WHITE); // Sinon, utiliser la couleur blanche pour l'intérieur
        }
        g.fillOval(x + 1, y + 1, DIAMETER - 2, DIAMETER - 2); // Dessiner l'intérieur du cercle

        // Centrer le nom du nœud
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(name);
        int textHeight = fm.getHeight();
        g.setColor(Color.BLACK); // Couleur noire pour le texte
        g.drawString(name, x + (DIAMETER - textWidth) / 2, y + ((DIAMETER - textHeight) / 2) + fm.getAscent());
    }
}



// Classe pour représenter une connexion
class Connection {
    Node start;
    Node end;

    public Connection(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

    // Dessine la connexion sur le panneau
    public void draw(Graphics g) {
    	 Graphics2D g2 = (Graphics2D) g.create();
         g2.setStroke(new BasicStroke(3)); // Augmenter l'épaisseur à 3
         g2.drawLine(start.x + 25, start.y + 25, end.x + 25, end.y + 25);
         g2.dispose(); // Libérer la copie après la modification
    }
}
class Light {
    Point position;
    Point start;
    Point end;
    final int radius = 10;
    Color color = Color.YELLOW;
    
    public Light(Point start, Point end) {
        this.position = new Point(start);
        this.start = start;
        this.end = end;
    }
    
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);
    }
    
    public boolean move() {
        if (position.equals(end)) {
            return false; // Return false when the light has reached its destination
        }
        
        int xDirection = Integer.compare(end.x, position.x);
        int yDirection = Integer.compare(end.y, position.y);
        position.translate(xDirection, yDirection);
        return !position.equals(end);
    }

}

// Panneau personnalisé pour le dessin
class NetworkPanel extends JPanel implements GraphicalNetworkInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	   private static final int PREF_WIDTH = 1000;
	    private static final int PREF_HEIGHT = 800;
	    private final List<Node> nodes = new ArrayList<>();
	    private final List<Connection> connections = new ArrayList<>();
	    private List<Light> lights = new ArrayList<>();
	    private Timer animationTimer;
	    int gridSize = 100;
	    private boolean isOn = true;
	    
    public NetworkPanel() {
    	setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT)); // Fixer la taille préférée
        Timer blinkTimer = new Timer(500, e -> {
            isOn = !isOn;
            repaint();
        });
        blinkTimer.start();
    }
    
    // Méthode pour trouver un nœud par son nom
    public Node findNodeByName(String name) {
        for (Node node : nodes) {
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null; // Retourner null si le nœd n'est pas trouvé
    }
    public void startLightAnimation(Node start, Node end) {
        Point startPoint = new Point(start.x + Node.DIAMETER / 2, start.y + Node.DIAMETER / 2);
        Point endPoint = new Point(end.x + Node.DIAMETER / 2, end.y + Node.DIAMETER / 2);
        lights.add(new Light(startPoint, endPoint));
        
        if (animationTimer == null || !animationTimer.isRunning()) {
            int delay = 10; // milliseconds
            animationTimer = new Timer(delay, e -> {
            	for (java.util.Iterator<Light> iterator = lights.iterator(); iterator.hasNext();) {
                    Light light = iterator.next();
                    boolean isMoving = light.move();
                    if (!isMoving) {
                        iterator.remove(); // Remove the light when it reaches its destination
                    }
                }
                repaint();

                if (lights.isEmpty()) {
                    ((Timer) e.getSource()).stop(); // Stop the timer if there are no more lights
                }
            });

            animationTimer.start();
        }
    }
    
    // Méthode pour dessiner les axes et les lignes pointillées
    private void drawGrid(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        g2d.setColor(Color.BLACK);
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);

        // Dessin des lignes horizontales et des étiquettes des axes Y
        for (int i = gridSize; i < height; i += gridSize) {
            int yLabel = height - i;
            g2d.drawLine(0, i, width, i);
            String label = String.valueOf(yLabel / gridSize);
            g2d.drawString(label, 5, i + (g2d.getFontMetrics().getAscent() - g2d.getFontMetrics().getDescent())/2);
        }

        // Dessin des lignes verticales et des étiquettes des axes X
        for (int i = gridSize; i < width; i += gridSize) {
            g2d.drawLine(i, 0, i, height);
            String label = String.valueOf(i / gridSize);
            g2d.drawString(label, i - g2d.getFontMetrics().stringWidth(label)/2, height - 5);
        }
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        drawGrid(g2d);

        for (Connection conn : connections) {
            conn.draw(g2d);
        }
        for (Node node : nodes) {
            node.draw(g2d, isOn);
        }
        
        for (Light light : lights) {
            light.draw(g2d);
        }

        g2d.dispose();
    }

 // Ajoute un nœud au réseau
    public void addNode(String name, double gridX, double gridY) {
        int pixelX = (int) (gridX * gridSize); // Coordonnée X centrée sur la grille
        // Coordonnée Y inversée et centrée sur la grille (en tenant compte de la taille du nœud pour le centrage)
        int pixelY = (int) (getHeight() - gridY * gridSize);
        // Ajout du nœud avec correction pour le diamètre du nœud
        nodes.add(new Node(name, pixelX - (50 / 2), pixelY - (50/2)));
        repaint();
    }

    // Méthode pour ajouter une connexion par les noms des nœuds
    public void addConnection(String startName, String endName) {
        Node startNode = findNodeByName(startName);
        Node endNode = findNodeByName(endName);
        if (startNode != null && endNode != null) {
            connections.add(new Connection(startNode, endNode));
            repaint();
        }
    }
    @Override
    public void addGraphicalNode(String name, double x, double y) {
        this.addNode(name, x, y);
    }
    
    @Override
    public void addGraphicalConnection(String startName, String endName) {
        this.addConnection(startName, endName);
    }
    
 // Méthode pour supprimer une connexion par les noms des nœuds
    public void removeGraphicalConnection(String startName, String endName) {
        // Rechercher la connexion à supprimer
        for (java.util.Iterator<Connection> iterator = connections.iterator(); iterator.hasNext();) {
            Connection conn = iterator.next();
            if (conn.start.name.equals(startName) && conn.end.name.equals(endName)) {
                // Supprimer la connexion trouvée
                iterator.remove();
                repaint(); // Redessiner pour mettre à jour l'affichage
                break; // Sortir de la boucle après avoir trouvé et supprimé la connexion
            }
        }
    }

    
    @Override
    public void startGraphicalLightAnimation(String startName, String endName) {
        Node startNode = this.findNodeByName(startName);
        Node endNode = this.findNodeByName(endName);
        if (startNode != null && endNode != null) {
            this.startLightAnimation(startNode, endNode);
        }
    }
    
    public void toggleNodeBlinking(String nodeName) {
        Node node = findNodeByName(nodeName);
        if (node != null) {
            node.toggleBlinking(); // Basculer l'état de clignotement du nœud
        }
    }



}

public class NetworkGraph {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Network Graph");
            NetworkPanel panel = new NetworkPanel();

            frame.setSize(800, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Ajouter un formulaire pour la création de nœuds
            JPanel formPanel = new JPanel(new GridLayout(0, 2));

            JTextField nameField = new JTextField();
            JTextField xField = new JTextField();
            JTextField yField = new JTextField();
            JButton addNodeButton = new JButton("Add Node");

            JTextField startNodeField = new JTextField();
            JTextField endNodeField = new JTextField();
            JButton connectNodesButton = new JButton("Connect Nodes");

            formPanel.add(new JLabel("Node Name:"));
            formPanel.add(nameField);
            formPanel.add(new JLabel("X Coordinate:"));
            formPanel.add(xField);
            formPanel.add(new JLabel("Y Coordinate:"));
            formPanel.add(yField);
            formPanel.add(addNodeButton);

            // Champs pour la connexion des nœuds
            formPanel.add(new JLabel("Start Node Name:"));
            formPanel.add(startNodeField);
            formPanel.add(new JLabel("End Node Name:"));
            formPanel.add(endNodeField);
            formPanel.add(connectNodesButton);

            // Logique pour ajouter un nœud au panel
            addNodeButton.addActionListener(e -> {
                String name = nameField.getText();
                try {
                    double x = Double.parseDouble(xField.getText());
                    double y = Double.parseDouble(yField.getText());
                    panel.addGraphicalNode(name, x, y);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                        "X and Y coordinates must be valid numbers.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            // Logique pour connecter deux nœuds
            connectNodesButton.addActionListener(e -> {
                String startNodeName = startNodeField.getText();
                String endNodeName = endNodeField.getText();
                panel.addGraphicalConnection(startNodeName, endNodeName);
            });

            frame.add(formPanel, BorderLayout.NORTH);
            frame.add(panel, BorderLayout.CENTER);

            JButton animateButton = new JButton("Animate Light");
            animateButton.addActionListener(e -> {
                // Exemple : changer pour récupérer les noms à partir des champs de texte ou autre
                String startName = "n1";
                String endName = "n2";
                panel.startGraphicalLightAnimation(startName, endName);
            });

            frame.add(animateButton, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }
}

