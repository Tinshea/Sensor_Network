package Tests.AST.CEXP;


import AST.CEXP.GCExp;
import AST.Rand.CRand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GCExpTest {

    @Test
    public void testSuperioriteVraie() {
        // Initialiser deux instances de CRand, où rand1 a une valeur plus grande que rand2
        double valeurGrande = 45.0;
        double valeurPetite = 30.0;
        CRand rand1 = new CRand(valeurGrande);
        CRand rand2 = new CRand(valeurPetite);
        GCExp gcExp = new GCExp(rand1, rand2);

        // Evaluation
        boolean resultat = gcExp.eval(null);

        // Vérification que le résultat est vrai
        assertTrue(resultat, "GCExp devrait retourner vrai lorsque la valeur de rand1 est supérieure à celle de rand2.");
    }

    @Test
    public void testSuperioriteFausse() {
        // Initialiser deux instances de CRand, où rand1 a une valeur plus petite que rand2
        double valeurPetite = 20.0;
        double valeurGrande = 40.0;
        CRand rand1 = new CRand(valeurPetite);
        CRand rand2 = new CRand(valeurGrande);
        GCExp gcExp = new GCExp(rand1, rand2);

        // Evaluation
        boolean resultat = gcExp.eval(null);

        // Vérification que le résultat est faux
        assertFalse(resultat, "GCExp devrait retourner faux lorsque la valeur de rand1 est inférieure à celle de rand2.");
    }

    @Test
    public void testEgalite() {
        // Tester l'égalité des valeurs pour voir si elle retourne faux
        double valeurEgale = 25.0;
        CRand rand1 = new CRand(valeurEgale);
        CRand rand2 = new CRand(valeurEgale);
        GCExp gcExp = new GCExp(rand1, rand2);

        // Evaluation
        boolean resultat = gcExp.eval(null);

        // Vérification que le résultat est faux
        assertFalse(resultat, "GCExp devrait retourner faux lorsque les valeurs de rand1 et rand2 sont égales.");
    }
}
