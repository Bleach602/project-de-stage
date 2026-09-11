package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.SessionCandidatureDTO;
import IFFPO_Web_Platform.entity.Filiere;
import IFFPO_Web_Platform.entity.SessionCandidature;
import IFFPO_Web_Platform.entity.enums.StatutFiliere;
import IFFPO_Web_Platform.entity.enums.StatutSession;
import IFFPO_Web_Platform.repository.FiliereRepository;
import IFFPO_Web_Platform.repository.SessionRepository;
import IFFPO_Web_Platform.service.SessionCandidatureService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SessionCandidatureServiceImpl implements SessionCandidatureService {

    private final SessionRepository sessionRepository;
    private final FiliereRepository filiereRepository;

    public SessionCandidatureServiceImpl(SessionRepository sessionRepository, FiliereRepository filiereRepository) {
        this.sessionRepository = sessionRepository;
        this.filiereRepository = filiereRepository;
    }

    //CONVERTIR DTO EN ENTITY SANS LE STATUT DE LA SESSION
    public SessionCandidature toEntity(SessionCandidatureDTO dto){

        if (dto == null){
            return null;
        }

        SessionCandidature sessionCandidature = new SessionCandidature();

        sessionCandidature.setId(dto.getId());
        sessionCandidature.setAnneeAcademique(dto.getAnneeAcademique());
        sessionCandidature.setDateOuverture(dto.getDateOuverture());
        sessionCandidature.setDateFermeture(dto.getDateFermeture());

        if (dto.getFiliereIds() != null && !dto.getFiliereIds().isEmpty()){
            sessionCandidature.setFilieres(new HashSet<>(
                    filiereRepository.findAllById(dto.getFiliereIds())
            ));
        }

        return sessionCandidature;
    }

    //CONVERTIR ENTITY SessionCandidature EN DTO SessionCandidatureDTO
    public SessionCandidatureDTO toDTO(SessionCandidature entity){

        SessionCandidatureDTO sessionCandidatureDTO = new SessionCandidatureDTO();

        sessionCandidatureDTO.setId(entity.getId());
        sessionCandidatureDTO.setAnneeAcademique(entity.getAnneeAcademique());
        sessionCandidatureDTO.setDateOuverture(entity.getDateOuverture());
        sessionCandidatureDTO.setDateFermeture(entity.getDateFermeture());
        sessionCandidatureDTO.setStatutSession(entity.getStatutSession());

        Set<Long> ids = new HashSet<>();
       if (entity.getFilieres() != null){
           entity.getFilieres().forEach(f->
                   ids.add(f.getId()));
       }
       sessionCandidatureDTO.setFiliereIds(ids);

        return sessionCandidatureDTO;
    }

    @Override
    public Page<SessionCandidatureDTO> findSessionPagined(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        return sessionRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public SessionCandidatureDTO trouverParId(Long id) {

        SessionCandidature sessionCandidature = sessionRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Session" + id + "non trouvée"));

        return toDTO(sessionCandidature);
    }

    @Override
    @Transactional
    public List<SessionCandidatureDTO> findAllsSessionCandidature() {

        List<SessionCandidature> sessionCandidatures = sessionRepository.findAll();
        return sessionCandidatures.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public SessionCandidatureDTO createSessionCandidature(SessionCandidatureDTO sessionCandidatureDTO) {

        //OUVERTE par defaut
        if (sessionCandidatureDTO.getStatutSession() == null){
            sessionCandidatureDTO.setStatutSession(StatutSession.OUVERTE);
        }

        //1. ANNEE ACADEMIQUE UNIQUE
        if (sessionRepository.existsByAnneeAcademique(sessionCandidatureDTO.getAnneeAcademique())){
            throw new RuntimeException("Une session existe déjà pour l'année académique " +
                    sessionCandidatureDTO.getAnneeAcademique());
        }

        //2. Une seule session peut etre ouverte à un instant donné...
        if (sessionCandidatureDTO.getStatutSession() == StatutSession.OUVERTE){

            boolean oneSessionAlreadyOpen = sessionRepository.existsByStatutSession(StatutSession.OUVERTE);

            if (oneSessionAlreadyOpen){
                throw new RuntimeException("Impossible d'ouvrir cette session. Une autre session est déjà ouverte.");
            }
        }

        //3. Selection et verification de la presence d'au moins une filiere
        if (sessionCandidatureDTO.getFiliereIds() == null || sessionCandidatureDTO.getFiliereIds().isEmpty()){
            throw new RuntimeException("Une session doit contenir au moins une filière pour etre ouverte.");
        }

        Set<Filiere> filieresSelectionnees = new HashSet<>();

        for (Long filiereId : sessionCandidatureDTO.getFiliereIds()){

            Filiere filiere = filiereRepository.findById(filiereId)
                    .orElseThrow(()-> new RuntimeException("Filière" + filiereId + "non trouvée"));

            //SEULES LES FILIERES OUVERTE PEUVENT ETRE AJOUTEE
            if (filiere.getStatutFiliere() == StatutFiliere.FERMEE){
                throw new RuntimeException("Ajout impossible de la filière " + filiere.getNom() + " car elle est fermée");
            }
            filieresSelectionnees.add(filiere);
        }

        SessionCandidature session = new SessionCandidature();
        session.setAnneeAcademique(sessionCandidatureDTO.getAnneeAcademique());
        session.setDateOuverture(sessionCandidatureDTO.getDateOuverture());
        session.setDateFermeture(sessionCandidatureDTO.getDateFermeture());
        session.setStatutSession(sessionCandidatureDTO.getStatutSession());
        session.setFilieres(filieresSelectionnees);

        SessionCandidature savedSession = sessionRepository.save(session);

        return toDTO(savedSession);
    }

    @Override
    @Transactional
    public SessionCandidatureDTO updateSessionCandidature(Long id, SessionCandidatureDTO sessionCandidatureDTO) {

        SessionCandidature sessionCandidature = sessionRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Session" + id + "non trouvée"));

        //INTERDIRE LA MODIFICATION D'UNE SESSION FERMEE
        if (sessionCandidature.getStatutSession() == StatutSession.FERMEE){
                throw new RuntimeException("Modification impossible car session FERMEE");
        }

        //CHARGER LES NOUVELLES FILIERES...
        Set<Filiere> newFilieres = new HashSet<>();
        for (Long filiereId : sessionCandidatureDTO.getFiliereIds()){

            Filiere filiere = filiereRepository.findById(filiereId)
                    .orElseThrow(()-> new RuntimeException("Filière" + filiereId + "non trouvée"));

            if (filiere.getStatutFiliere() == StatutFiliere.FERMEE){
                throw new RuntimeException("Ajout" + filiere.getNom() + "impossible car elle est fermée");
            }
            newFilieres.add(filiere);
        }

        sessionCandidature.setDateFermeture(sessionCandidatureDTO.getDateFermeture());
        sessionCandidature.setFilieres(newFilieres);

        //MAJ des filières associées si necessaires
        if (sessionCandidatureDTO.getFiliereIds() != null){
            List<Filiere> newFiliere = filiereRepository.findAllById(sessionCandidatureDTO.getFiliereIds());
            sessionCandidature.setFilieres(new HashSet<>(newFiliere));
        }

        SessionCandidature updatedSession = sessionRepository.save(sessionCandidature);
        return toDTO(updatedSession);
    }

    @Override
    @Transactional
    public void openSessionCandidature(Long id) {

        //RECUP SESSION
        SessionCandidature session = sessionRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Session" + id + "non trouvée"));

        if (session.getFilieres() == null || session.getFilieres().isEmpty()){
            throw new RuntimeException("Impossible d'ouvrir cette session car elle n'a aucune filiere.");

        }

        boolean oneSessionAlreadyOpen =  sessionRepository.existsByStatutSession(StatutSession.OUVERTE);
        if (oneSessionAlreadyOpen){
            throw new RuntimeException("Impossible d'ouvrir cette session. Une autre session est actuellement déjà ouverte.");
        }

        session.setStatutSession(StatutSession.OUVERTE);
        sessionRepository.save(session);
    }

    @Override
    @Transactional
    public void closeSessionCandidature(Long id) {

        SessionCandidature session = sessionRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Session" + id + "non trouvée"));

        session.setStatutSession(StatutSession.FERMEE);
        sessionRepository.save(session);

    }




    // pour moi
    @Override
    public SessionCandidatureDTO getSessionActive() {

        return sessionRepository.findByStatutSession(StatutSession.OUVERTE)
                .map(session-> {
                    SessionCandidatureDTO dto = new SessionCandidatureDTO();
                    dto.setId(session.getId());
                    dto.setAnneeAcademique(session.getAnneeAcademique());
                    dto.setDateOuverture(session.getDateOuverture());
                    dto.setDateFermeture(session.getDateFermeture());
                    dto.setStatutSession(session.getStatutSession());

                    return dto;
                }).orElse(null);
    }

    @Override
    public boolean isSessionOuverte() {

        SessionCandidatureDTO session = getSessionActive();

        if(session == null)
            return false;

        LocalDate aujourdHui = LocalDate.now();

//            Même si on oubli de changer  le statut à apèrs la fin.
        return !aujourdHui.isBefore(session.getDateOuverture())
                && !aujourdHui.isAfter(session.getDateFermeture());

    }

}
