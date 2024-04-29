package Tests.AST.CEXP;

import AST.CEXP.EqCExp;
import AST.Rand.CRand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EqCExpTest {

    @Test
    public void testEgaliteDesValeurs() {
        // Donner deux instances de CRand qui retournent la même valeur
        double valeur = 42.0;
        CRand rand1 = new CRand(valeur);
        CRand rand2 = new CRand(valeur);
        EqCExp eqCExp = new EqCExp(rand1, rand2);

        // Evaluer
        boolean resultat = eqCExp.eval(null);

        // Vérifier que le résultat est vrai
        assertTrue(resultat, "EqCExp devrait retourner vrai lorsque les deux instances de CRand évaluent à la même valeur.");
    }

    @Test
    public void testInegaliteDesValeurs() {
        // Donner deux instances de CRand qui retournent des valeurs différentes
        CRand rand1 = new CRand(42.0);
        CRand rand2 = new CRand(43.0);
        EqCExp eqCExp = new EqCExp(rand1, rand2);

        // Evaluer
        boolean resultat = eqCExp.eval(null);

        // Vérifier que le résultat est faux
        assertFalse(resultat, "EqCExp devrait retourner faux lorsque les deux instances de CRand évaluent à des valeurs différentes.");
    }
}
