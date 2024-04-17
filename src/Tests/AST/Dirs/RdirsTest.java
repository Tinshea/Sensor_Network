package Tests.AST.Dirs;


import AST.Dirs.IDirs;
import AST.Dirs.Rdirs;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class RdirsTest {

    private Rdirs rdirs;
    private IDirs baseDirs;
    private Direction addedDirection;

    @BeforeEach
    void setUp() {
        // Création d'une implémentation de base de IDirs qui retourne un ensemble fixe de directions
        baseDirs = new IDirs() {
            @Override
            public Set<Direction> eval() {
                Set<Direction> baseDirections = new HashSet<>();
                baseDirections.add(Direction.NE);
                baseDirections.add(Direction.SE);
                return baseDirections;
            }
        };

        // Direction à ajouter
        addedDirection = Direction.NW;

        // Création de Rdirs avec la direction ajoutée et l'implémentation de base
        rdirs = new Rdirs(addedDirection, baseDirs);
    }

    @Test
    void testEvalCombinesDirectionsCorrectly() {
        // Évaluation
        Set<Direction> result = rdirs.eval();

        // Vérification que le résultat contient toutes les directions attendues
        assertTrue(result.contains(Direction.NE), "Le résultat devrait contenir NE.");
        assertTrue(result.contains(Direction.SE), "Le résultat devrait contenir SE.");
        assertTrue(result.contains(addedDirection), "Le résultat devrait contenir NW.");
        assertEquals(3, result.size(), "Le résultat devrait contenir exactement trois directions.");
    }
}
