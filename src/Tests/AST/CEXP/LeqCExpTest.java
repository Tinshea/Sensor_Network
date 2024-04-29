package Tests.AST.CEXP;


import AST.CEXP.LeqCExp;
import AST.Rand.CRand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LeqCExpTest {

    @Test
    public void testInferioriteOuEgaliteVraie_Inferiorite() {
        // Initialiser deux instances de CRand où rand1 est inférieur à rand2
        CRand rand1 = new CRand(10.0);
        CRand rand2 = new CRand(20.0);
        LeqCExp leqCExp = new LeqCExp(rand1, rand2);

        // Évaluation
        boolean resultat = leqCExp.eval(null);

        // Vérification que le résultat est vrai
        assertTrue(resultat, "LeqCExp devrait retourner vrai lorsque la valeur de rand1 est inférieure à celle de rand2.");
    }

    @Test
    public void testInferioriteOuEgaliteVraie_Egalite() {
        // Initialiser deux CRand avec les mêmes valeurs
        CRand rand1 = new CRand(30.0);
        CRand rand2 = new CRand(30.0);
        LeqCExp leqCExp = new LeqCExp(rand1, rand2);

        // Évaluation
        boolean resultat = leqCExp.eval(null);

        // Vérification que le résultat est vrai
        assertTrue(resultat, "LeqCExp devrait retourner vrai lorsque les valeurs de rand1 et rand2 sont égales.");
    }

    @Test
    public void testSuperioriteFausse() {
        // Initialiser deux CRand où rand1 est supérieur à rand2
        CRand rand1 = new CRand(50.0);
        CRand rand2 = new CRand(40.0);
        LeqCExp leqCExp = new LeqCExp(rand1, rand2);

        // Évaluation
        boolean resultat = leqCExp.eval(null);

        // Vérification que le résultat est faux
        assertFalse(resultat, "LeqCExp devrait retourner faux lorsque la valeur de rand1 est supérieure à celle de rand2.");
    }
}
