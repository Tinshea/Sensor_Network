package app.Components;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public class Main {
    public static void main(String[] args) {
        // Créer deux instances de Position
        Position position1 = new Position(0, 0); // Origine
        Position position2 = new Position(1, 1); // Nord-Est par rapport à position1
        Position position3 = new Position(-1, 1); // Nord-Ouest par rapport à position1
        Position position4 = new Position(1, -1); // Sud-Est par rapport à position1
        Position position5 = new Position(-1, -1); // Sud-Ouest par rapport à position1

        // Tester la méthode directionFrom
        Direction direction = position1.directionFrom(position2);
        System.out.println("Position2 est " + direction + " par rapport à Position1.");

        direction = position1.directionFrom(position3);
        System.out.println("Position3 est " + direction + " par rapport à Position1.");

        direction = position1.directionFrom(position4);
        System.out.println("Position4 est " + direction + " par rapport à Position1.");

        direction = position1.directionFrom(position5);
        System.out.println("Position5 est " + direction + " par rapport à Position1.");
        
        double distance = position1.distance(position2);
        System.out.println("La distance entre position1 et position2 est : " + distance);
    }
}
