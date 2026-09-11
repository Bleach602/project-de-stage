package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.entity.UtilisateurValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UtilisateurValidatorTest {

    @Test
    void testerEmailValide(){

       // Arrange
        UtilisateurValidator user = new  UtilisateurValidator();

        //Act

        boolean email = user.emailValide("Arafat@gmail.com");

        //Assert

        assertTrue(email);

    }

    @Test
    void testerEmailInvalideValide(){

        // Arrange
        UtilisateurValidator user = new  UtilisateurValidator();

        //Act

        boolean email = user.emailValide("Arafat.com");

        //Assert

        assertFalse(email);

    }
}
