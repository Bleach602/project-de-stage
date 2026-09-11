package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.entity.Calculatrice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CalculatriceTest
{

//    ARRANGE =  préparer
//     ACT = éxécuter
//    ASSERT = verifier

    @Test
   void  additionnerDeuxNombres(){

        // je pose
        Calculatrice calculatrice = new Calculatrice();

        // j'appel le test
        int resultatAdd = calculatrice.additionner(3 ,5);

        //vérifications

        assertEquals(8, resultatAdd);

    }

    @Test
    void soustraireDeuxNombre(){

        Calculatrice calculatrice = new Calculatrice();

        int x = calculatrice.soustraire(5, 3);

        assertEquals(2, x);

    }

    @Test
    void MultiplierDeuxnbre(){
        Calculatrice calculatrice = new Calculatrice();

        int x = calculatrice.multiplier(2, 2);

        assertEquals(4, x);

    }
}
