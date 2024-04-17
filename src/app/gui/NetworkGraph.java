package app.gui;

import javax.swing.*;
import java.awt.*;


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

