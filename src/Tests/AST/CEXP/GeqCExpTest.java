package Tests.AST.CEXP;


import AST.CEXP.GeqCExp;
import AST.Rand.CRand;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeqCExpTest {

    @Test
    public void testSuperioriteOuEgaliteVraie_Superiorite() {
        // Initialiser deux CRand avec rand1 > rand2
        CRand rand1 = new CRand(50.0);
        CRand rand2 = new CRand(30.0);
        GeqCExp geqCExp = new GeqCExp(rand1, rand2);

        // Évaluation
        boolean resultat = geqCExp.eval(null);

        // Vérification que le résultat est vrai
        assertTrue(resultat, "GeqCExp devrait retourner vrai quand la valeur de rand1 est supérieure à celle de rand2.");
    }

    @Test
    public void testSuperioriteOuEgaliteVraie_Egalite() {
        // Initialiser deux CRand avec rand1 == rand2
        CRand rand1 = new CRand(40.0);
        CRand rand2 = new CRand(40.0);
        GeqCExp geqCExp = new GeqCExp(rand1, rand2);

        // Évaluation
        boolean resultat = geqCExp.eval(null);

        // Vérification que le résultat est vrai
        assertTrue(resultat, "GeqCExp devrait retourner vrai quand les valeurs de rand1 et rand2 sont égales.");
    }

    @Test
    public void testInferioriteFausse() {
        // Initialiser deux CRand avec rand1 < rand2
        CRand rand1 = new CRand(20.0);
        CRand rand2 = new CRand(40.0);
        GeqCExp geqCExp = new GeqCExp(rand1, rand2);

        // Évaluation
        boolean resultat = geqCExp.eval(null);

        // Vérification que le résultat est faux
        assertFalse(resultat, "GeqCExp devrait retourner faux quand la valeur de rand1 est inférieure à celle de rand2.");
    }
}

