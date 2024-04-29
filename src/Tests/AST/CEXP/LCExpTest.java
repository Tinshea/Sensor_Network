package Tests.AST.CEXP;


import AST.CEXP.LCExp;
import AST.Rand.CRand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LCExpTest {

    @Test
    public void testInferioriteVraie() {
        // Initialiser deux instances de CRand où rand1 est inférieur à rand2
        CRand rand1 = new CRand(10.0);
        CRand rand2 = new CRand(20.0);
        LCExp lcExp = new LCExp(rand1, rand2);

        // Évaluation
        boolean resultat = lcExp.eval(null);

        // Vérification que le résultat est vrai
        assertTrue(resultat, "LCExp devrait retourner vrai lorsque la valeur de rand1 est inférieure à celle de rand2.");
    }

    @Test
    public void testEgaliteFausse() {
        // Initialiser deux CRand avec les mêmes valeurs
        CRand rand1 = new CRand(30.0);
        CRand rand2 = new CRand(30.0);
        LCExp lcExp = new LCExp(rand1, rand2);

        // Évaluation
        boolean resultat = lcExp.eval(null);

        // Vérification que le résultat est faux
        assertFalse(resultat, "LCExp devrait retourner faux lorsque les valeurs de rand1 et rand2 sont égales.");
    }

    @Test
    public void testSuperioriteFausse() {
        // Initialiser deux CRand où rand1 est supérieur à rand2
        CRand rand1 = new CRand(50.0);
        CRand rand2 = new CRand(40.0);
        LCExp lcExp = new LCExp(rand1, rand2);

        // Évaluation
        boolean resultat = lcExp.eval(null);

        // Vérification que le résultat est faux
        assertFalse(resultat, "LCExp devrait retourner faux lorsque la valeur de rand1 est supérieure à celle de rand2.");
    }
}

