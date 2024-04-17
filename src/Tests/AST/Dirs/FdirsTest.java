package Tests.AST.Dirs;


import AST.Dirs.Fdirs;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

public class FdirsTest {

    @Test
    void testEvalSingleDirection() {
        // Initialisation de Fdirs avec une direction spécifique
        Fdirs fdirs = new Fdirs(Direction.NE);

        // Évaluation
        Set<Direction> result = fdirs.eval();

        // Vérifier que l'ensemble retourné contient exactement la direction spécifiée
        assertTrue(result.contains(Direction.NE), "L'ensemble devrait contenir la direction NE.");
        assertEquals(1, result.size(), "L'ensemble devrait contenir exactement une direction.");

        // Répéter le test pour chaque direction pour garantir la couverture complète
        Fdirs fdirsSE = new Fdirs(Direction.SE);
        Set<Direction> resultSE = fdirsSE.eval();
        assertTrue(resultSE.contains(Direction.SE), "L'ensemble devrait contenir la direction SE.");
        assertEquals(1, resultSE.size(), "L'ensemble devrait contenir exactement une direction.");

        Fdirs fdirsNW = new Fdirs(Direction.NW);
        Set<Direction> resultNW = fdirsNW.eval();
        assertTrue(resultNW.contains(Direction.NW), "L'ensemble devrait contenir la direction NW.");
        assertEquals(1, resultNW.size(), "L'ensemble devrait contenir exactement une direction.");

        Fdirs fdirsSW = new Fdirs(Direction.SW);
        Set<Direction> resultSW = fdirsSW.eval();
        assertTrue(resultSW.contains(Direction.SW), "L'ensemble devrait contenir la direction SW.");
        assertEquals(1, resultSW.size(), "L'ensemble devrait contenir exactement une direction.");
    }
}

