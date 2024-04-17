package Tests.AST.Query;

import AST.BEXP.Ibexp;
import AST.Cont.ICont;
import AST.Query.BQuery;
import app.Models.ExecutionState;
import app.Models.ProcessingNode;   
import app.Models.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class BQueryTest {

    private BQuery bQuery;
    private ExecutionState es;
    private final String nodeId = "node1";

    @BeforeEach
    void setUp() {
        // Implémentation basique de Ibexp
        Ibexp bexp = new Ibexp() {
            @Override
            public boolean eval(ExecutionStateI es) {
                return true; // Simule toujours vrai pour le test
            }
        };

        // Implémentation basique de ICont
        ICont cont = new ICont() {
            @Override
            public void eval(ExecutionStateI es) {
                // Aucun effet pour simplifier le test
            }
        };

        // Création d'un ProcessingNode simple
        ProcessingNode processingNode = new ProcessingNode(nodeId, null, null, null);

        // Initialisation de l'ExecutionState avec un ProcessingNode et un QueryResult vide
        es = new ExecutionState(processingNode, new QueryResult(new ArrayList<>(), new ArrayList<>()));

        // Initialisation de BQuery avec bexp et cont
        bQuery = new BQuery(bexp, cont);
    }

    @Test
    void testEvalExecutesContAndUpdatesResult() {
        QueryResultI result = bQuery.eval(es);
        System.out.println("isBoolean: " + result.isBooleanRequest()); // Doit afficher true si tout fonctionne correctement

        assertNotNull(result, "Le résultat ne doit pas être nul.");
        assertTrue(result.isBooleanRequest(), "Le résultat doit être identifié comme une requête booléenne.");
        // Assurez-vous que la liste des nœuds sensibles inclut le bon identifiant de nœud
        assertTrue(result.positiveSensorNodes().contains(nodeId), "La liste devrait contenir l'identifiant du nœud.");
    }

}
