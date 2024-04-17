package Tests.AST.Cont;

import AST.Cont.DCont;
import AST.Dirs.Fdirs;
import AST.Dirs.IDirs;
import AST.Dirs.Rdirs;
import app.Models.ExecutionState;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class DContTest {

    private DCont dCont;
    private ExecutionState es;
    private IDirs dirs;
    private final int maxSauts = 5;

    @BeforeEach
    void setUp() {
        // Configurer Fdirs pour retourner un ensemble initial de directions
        Fdirs fdirs = new Fdirs(Direction.NE); // Fdirs initialise avec la direction Nord-Est

        // Configurer Rdirs pour ajouter une direction supplémentaire
        Direction additionalDirection = Direction.SW; // Direction à ajouter
        dirs = new Rdirs(additionalDirection, fdirs); // Rdirs ajoute Sud-Ouest à l'ensemble retourné par Fdirs

        // Initialiser DCont avec Rdirs et un nombre maximal de sauts
        dCont = new DCont(dirs, maxSauts);

        // Initialiser l'état d'exécution
        es = new ExecutionState(null, null);
    }

    @Test
    void testEvalUpdatesExecutionState() {
        // Exécuter eval sur DCont
        dCont.eval(es);

        // Vérifier l'incrémentation des sauts (hops)
        assertEquals(1, es.getHops(), "Le nombre de sauts devrait être incrémenté de 1.");

        // Vérifier la configuration des sauts maximaux
        assertEquals(maxSauts, es.getMaxHops(), "Le nombre maximal de sauts devrait être mis à jour.");

        // Vérifier la mise à jour des directions
        Set<Direction> expectedDirections = new HashSet<>();
        expectedDirections.add(Direction.NE);
        expectedDirections.add(Direction.SW);
        assertEquals(expectedDirections, es.getDirections(), "Les directions devraient être mises à jour correctement avec NE et SW.");

        // Vérifier si l'état est bien passé en directionnel
        assertTrue(es.isDirectional(), "L'état devrait être marqué comme directionnel.");
    }
}

