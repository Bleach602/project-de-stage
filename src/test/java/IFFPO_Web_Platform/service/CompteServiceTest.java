    package IFFPO_Web_Platform.service;

    import IFFPO_Web_Platform.entity.CompteService;
    import org.junit.jupiter.api.Test;

    import static org.junit.jupiter.api.Assertions.*;

    public class CompteServiceTest {


        @Test
        void Testerretrai_avec_solde_supperieur_AuMontant(){

            //Arange
            CompteService compteService = new CompteService();

            //Act + Assert

            //lorsqu'une exception ne dois pas être  lévé
            assertDoesNotThrow(
                    () ->  compteService.retirer(100,50)
            );



        }

        @Test
        void TesteRetrai_avec_solde_inferieur_AuMontant(){

            //Arange
            CompteService compteService = new CompteService();

            //Act + Assert
    //        assertThrows(
    //                IllegalArgumentException.class, () ->{
    //                    compteService.retirer(50,100);
    //                }
    //        );

            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> compteService.retirer(50, 100)
                    );

            assertEquals(
                    "Solde insuffisant",
                    exception.getMessage()
            );

        }
    }
