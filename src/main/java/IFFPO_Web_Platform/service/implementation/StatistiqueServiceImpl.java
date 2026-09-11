package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.PointStatistiqueDTO;
import IFFPO_Web_Platform.dto.StatistiqueDTO;
import IFFPO_Web_Platform.dto.StatistiqueGraphiquesDTO;
import IFFPO_Web_Platform.entity.SessionCandidature;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import IFFPO_Web_Platform.entity.enums.StatutFiliere;
import IFFPO_Web_Platform.entity.enums.StatutSession;
import IFFPO_Web_Platform.repository.*;
import IFFPO_Web_Platform.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatistiqueServiceImpl implements StatistiqueService {

    private static final String ROLE_CANDIDAT = "ROLE_CANDIDAT";

    private final CandidatureRepository candidatureRepository;
    private final FiliereRepository filiereRepository;
    private final SpecialiteRepository specialiteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SessionRepository sessionRepository;

    @Override
    public StatistiqueDTO calculerStatistiques() {

        StatistiqueDTO dto = new StatistiqueDTO();

        long total = candidatureRepository.count();
        long enAttente = candidatureRepository.countByStatutCandidature(StatutCandidature.EN_ATTENTE);
        long validees = candidatureRepository.countByStatutCandidature(StatutCandidature.VALIDER);
        long rejetees = candidatureRepository.countByStatutCandidature(StatutCandidature.REJETEE);
        long enCorrection = candidatureRepository.countByStatutCandidature(StatutCandidature.EN_COURS_DE_CORRECTION);

        dto.setTotalCandidatures(total);
        dto.setCandidaturesEnAttente(enAttente);
        dto.setCandidaturesValidees(validees);
        dto.setCandidaturesValidees(rejetees);
        dto.setCandidaturesEnCorrection(enCorrection);

        dto.setTotalCandidats(utilisateurRepository.countByRole_Intitule(ROLE_CANDIDAT));

        dto.setTotalFilieres(filiereRepository.count());
        dto.setTotalSpecialites(specialiteRepository.count());
        dto.setFilieresOuvertes(filiereRepository.countByStatutFiliere(StatutFiliere.OUVERTE));
        dto.setFilieresFermees(filiereRepository.countByStatutFiliere(StatutFiliere.FERMEE));

        if (total > 0){
            dto.setTauxValidation(arrondir(validees * 100.0 / total));
            dto.setTauxRejet(arrondir(rejetees * 100.0 / total));
            dto.setTauxAttente(arrondir(enAttente * 100.0 / total));
        } else {
            dto.setTauxValidation(0);
            dto.setTauxRejet(0);
            dto.setTauxAttente(0);
        }

        return dto;
    }

    @Override
    public StatistiqueGraphiquesDTO calculerGraphiques(String periode) {

        LocalDate[] plage = resoudrePeriode(periode);
        LocalDate debut = plage[0];
        LocalDate fin = plage[1];

        StatistiqueGraphiquesDTO dto = new StatistiqueGraphiquesDTO();

        // 1. Evolution des candidatures par mois
        List<LocalDate> datesCandidatures = candidatureRepository.findDatesCandidatureEntrePeriode(debut, fin);
        dto.setEvolutionCandidatures(grouperParMois(datesCandidatures, debut, fin));

        // 2. Répartition par statut (sur la même période)
        long validees = candidatureRepository.countByStatutCandidatureAndDateCandidatureBetween(
                StatutCandidature.VALIDER, debut, fin);
        long enAttente = candidatureRepository.countByStatutCandidatureAndDateCandidatureBetween(
                StatutCandidature.EN_ATTENTE, debut, fin);
        long rejetees = candidatureRepository.countByStatutCandidatureAndDateCandidatureBetween(
                StatutCandidature.REJETEE, debut, fin);

        List<PointStatistiqueDTO> repartition = new ArrayList<>();
        repartition.add(new PointStatistiqueDTO("Validées", validees));
        repartition.add(new PointStatistiqueDTO("En attente", enAttente));
        repartition.add(new PointStatistiqueDTO("Rejetées", rejetees));
        dto.setRepartitionParStatut(repartition);

        // 3. Candidatures par filière
        List<Object[]> filiereRaw = candidatureRepository.countCandidaturesParFiliere(debut, fin);
        List<PointStatistiqueDTO> parFiliere = new ArrayList<>();
        for (Object[] ligne : filiereRaw) {
            parFiliere.add(new PointStatistiqueDTO((String) ligne[0], (Long) ligne[1]));
        }
        dto.setCandidaturesParFiliere(parFiliere);

        // 4. Evolution des inscriptions candidats
        LocalDateTime debutDateTime = debut.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        List<LocalDateTime> datesInscriptions = utilisateurRepository
                .findDatesInscriptionParRoleEntrePeriode(ROLE_CANDIDAT, debutDateTime, finDateTime);
        List<LocalDate> datesInscriptionsSimples = new ArrayList<>();
        for (LocalDateTime dt : datesInscriptions) {
            datesInscriptionsSimples.add(dt.toLocalDate());
        }
        dto.setEvolutionInscriptions(grouperParMois(datesInscriptionsSimples, debut, fin));

        return dto;
    }

    /*
     * Regroupe une liste de dates par mois, en incluant tous les mois de la période
     * (même ceux à 0), pour obtenir un axe continu comme dans la maquette.
     */
    private List<PointStatistiqueDTO> grouperParMois(List<LocalDate> dates, LocalDate debut, LocalDate fin) {

        Map<YearMonth, Long> compteurs = new LinkedHashMap<>();

        YearMonth moisDebut = YearMonth.from(debut);
        YearMonth moisFin = YearMonth.from(fin);

        for (YearMonth m = moisDebut; !m.isAfter(moisFin); m = m.plusMonths(1)) {
            compteurs.put(m, 0L);
        }

        for (LocalDate date : dates) {
            YearMonth mois = YearMonth.from(date);
            compteurs.merge(mois, 1L, Long::sum);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM", Locale.FRENCH);
        List<PointStatistiqueDTO> resultat = new ArrayList<>();

        for (Map.Entry<YearMonth, Long> entry : compteurs.entrySet()) {
            String label = entry.getKey().atDay(1).format(formatter);
            label = label.substring(0, 1).toUpperCase() + label.substring(1).replace(".", "");
            resultat.add(new PointStatistiqueDTO(label, entry.getValue()));
        }

        return resultat;
    }

    private LocalDate[] resoudrePeriode(String periode) {

        LocalDate aujourdHui = LocalDate.now();
        String cle = (periode == null) ? "annee" : periode;

        if ("session".equals(cle)) {

            Optional<SessionCandidature> sessionOuverte =
                    sessionRepository.findByStatutSession(StatutSession.OUVERTE);

            if (sessionOuverte.isPresent()) {
                SessionCandidature session = sessionOuverte.get();
                LocalDate finSession = session.getDateFermeture().isAfter(aujourdHui)
                        ? aujourdHui
                        : session.getDateFermeture();
                return new LocalDate[]{session.getDateOuverture(), finSession};
            }

            // Aucune session ouverte : on retombe sur "cette année"
            return new LocalDate[]{aujourdHui.withDayOfYear(1), aujourdHui};
        }

        LocalDate debut;

        switch (cle) {
            case "mois":
                debut = aujourdHui.withDayOfMonth(1);
                break;
            case "30jours":
                debut = aujourdHui.minusDays(30);
                break;
            case "annee":
            default:
                debut = aujourdHui.withDayOfYear(1);
                break;
        }

        return new LocalDate[]{debut, aujourdHui};
    }

    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}
