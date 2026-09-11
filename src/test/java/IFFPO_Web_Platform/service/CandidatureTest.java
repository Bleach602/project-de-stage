package IFFPO_Web_Platform.service;


import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.repository.CandidatureRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.implementation.CandidatureServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CandidatureTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks

    private CandidatureServiceImpl candidatureServiceImpl;

    @Test
    void doitRetournerCandidatureSiElleExiste() {

        //Arrange
        Candidature candidature = new Candidature();

        when(candidatureRepository.findById(1L))
                .thenReturn(Optional.of(candidature));

        //ACT

        Candidature resultat = candidatureServiceImpl.trouverParId(1L);

        //ASSERT
        assertNotNull(candidature);
        assertEquals(candidature, resultat);


    }

    @Test
    void doitLeverExceptionSiCandidatureIntrouvable() {
        //Arrange

        when(candidatureRepository.findById(1L))
                .thenReturn(Optional.empty());

        //ACt + ASSERT

        assertThrows(RuntimeException.class, () ->
                candidatureServiceImpl.trouverParId(1L));

        //verify
        verify(candidatureRepository, times(1))
                .findById(1L);
    }

    @Test
    void neDoitPasLeverExceptionSiCandidatureEnAttente() {

        //Arrange

        Candidature candidature = new Candidature();
        candidature.setStatutCandidature(StatutCandidature.EN_ATTENTE);

        //ACt +ASSErt

        assertDoesNotThrow(() ->
                candidatureServiceImpl.verifierTransitionDepuisEnAttente(candidature)
        );

    }

    @Test
    void doitLeverExceptionSiCandidatureDejaValidee() {

        //Arrange
        Candidature candidature = new Candidature();
        candidature.setStatutCandidature(StatutCandidature.VALIDER);

        //ACT + ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                candidatureServiceImpl.verifierTransitionDepuisEnAttente(candidature)
        );

        //verif
        assertEquals("Action possible que depuis le statut EN_ATTENTE Statut actuel : VALIDER",
                exception.getMessage());
    }

    @Test
    void doitValiderCandidatureEnAttente() {

        // ARRANGE
        Candidature candidature = new Candidature();
        candidature.setStatutCandidature(StatutCandidature.EN_ATTENTE);

        when(candidatureRepository.findById(1L))
                .thenReturn(Optional.of(candidature));


// ACT
        candidatureServiceImpl.valider(1L);


// ASSERT
        assertEquals(
                StatutCandidature.VALIDER,
                candidature.getStatutCandidature()
        );

        assertNotNull(candidature.getDateValidation());


// VERIFY
        verify(candidatureRepository, times(1))
                .save(candidature);
    }

    @Test
    void doitLeverExceptionLorsquOnValideUneCandidatureRejetee(){

        //ARRANGE
        Candidature candidature = new Candidature();
        candidature.setStatutCandidature(StatutCandidature.REJETEE);

        when(candidatureRepository.findById(1L))
                .thenReturn(Optional.of(candidature));

        //Act + ASSERT

        assertThrows(IllegalStateException.class, ()->
                candidatureServiceImpl.valider(1L)
        );

        //verify
        verify(candidatureRepository,never())
                .save(candidature);

    }

    @Test
    void doitLeverExceptionLorsquOnValideUneCandidatureDejaValidee(){
        //ARRANGE
        Candidature candidature = new Candidature();
        candidature.setStatutCandidature(StatutCandidature.VALIDER);

        when(candidatureRepository.findById(1L))
                .thenReturn(Optional.of(candidature));

        //Act + ASSERT

        assertThrows(IllegalStateException.class, ()->
                candidatureServiceImpl.valider(1L)
        );

        //verify
        verify(candidatureRepository,never())
                .save(candidature);
    }
}
