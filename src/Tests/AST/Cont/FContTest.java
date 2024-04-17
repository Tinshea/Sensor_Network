package Tests.AST.Cont;

import AST.Cont.FCont;
import AST.Base.IBase;
import app.Models.ExecutionState;
import app.Models.Position;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FContTest {

    private FCont fCont;
    private ExecutionState es;
    private IBase mockBase;
    private final double distanceMax = 100.0;
    private PositionI expectedPosition;

    @BeforeEach
    void setUp() {
        // Configuration d'une position attendue
        expectedPosition = new Position(50.0, 50.0);

        // Création d'un mock simple pour IBase
        mockBase = new IBase() {
            @Override
            public PositionI eval(ExecutionStateI es) {
                return expectedPosition;
            }
        };

        // Initialisation de FCont avec le mock de IBase et une distance maximale
        fCont = new FCont(mockBase, distanceMax);

        // Initialisation de l'état d'exécution
        es = new ExecutionState(null, null);
    }

    @Test
    void testEvalSetsExecutionStateCorrectly() {
        // Exécution de eval
        fCont.eval(es);

        // Vérification que la distance maximale est correctement configurée
        assertEquals(distanceMax, es.getMaxDistance(), "La distance maximale devrait être mise à jour.");

        // Vérification que la position est correctement configurée
        assertEquals(expectedPosition, es.getPosition(), "La position devrait être mise à jour.");

        // Vérification que l'état de flooding est activé
        assertTrue(es.isFlooding(), "L'état de flooding devrait être activé.");
    }
}
