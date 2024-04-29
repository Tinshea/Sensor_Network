package Tests.AST.BEXP;


import AST.BEXP.CExpBExp;
import AST.CEXP.GeqCExp;
import AST.CEXP.LeqCExp;
import AST.Rand.CRand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CExpBExpTest {

    @Test
    public void testCExpBExpTrue() {
        // Configuration pour retourner true
        double trueValue = 1.0;  // Supposons que 1.0 est interprété comme true
        CRand randTrue = new CRand(trueValue);
        GeqCExp geqCExpTrue = new GeqCExp(randTrue, randTrue);  // GeqCExp retournera true si les valeurs sont égales

        CExpBExp cExpBExpTrue = new CExpBExp(geqCExpTrue);

        // Évaluation
        boolean result = cExpBExpTrue.eval(null);

        // Vérification que le résultat est vrai
        assertTrue(result, "CExpBExp devrait retourner vrai quand l'expression CExp évaluée est vraie.");
    }

    @Test
    public void testCExpBExpFalse() {
        // Configuration pour retourner false
        double trueValue = 1.0;
        double falseValue = 0.0;  // Supposons que 0.0 est interprété comme false
        CRand randTrue = new CRand(trueValue);
        CRand randFalse = new CRand(falseValue);
        LeqCExp geqCExpFalse = new LeqCExp(randTrue, randFalse);  // GeqCExp retournera false si randTrue n'est pas <= randFalse

        CExpBExp cExpBExpFalse = new CExpBExp(geqCExpFalse);

        // Évaluation
        boolean result = cExpBExpFalse.eval(null);

        // Vérification que le résultat est faux
        assertFalse(result, "CExpBExp devrait retourner faux quand l'expression CExp évaluée est fausse.");
    }
}
