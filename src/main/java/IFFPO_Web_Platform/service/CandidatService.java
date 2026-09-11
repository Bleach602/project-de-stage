package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.entity.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CandidatService {

    List<Utilisateur> findAll();
    Utilisateur findByEmail(String email);

}
