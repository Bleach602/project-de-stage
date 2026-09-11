package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.repository.CandidatRepository;
import IFFPO_Web_Platform.service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidatServiceImpl implements CandidatService {


    @Autowired
    private CandidatRepository candidatRepository;

    @Override
    public List<Utilisateur> findAll() {
        return candidatRepository.findAll();
    }

    @Override
    public Utilisateur findByEmail(String email) {
        return candidatRepository.findByEmail(email);
    }
}
