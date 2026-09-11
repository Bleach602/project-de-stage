package IFFPO_Web_Platform.specification;

import IFFPO_Web_Platform.entity.Candidature;
import IFFPO_Web_Platform.entity.enums.StatutCandidature;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CandidatureSpecification {

    public static Specification<Candidature> avecFiltres(Long sessionId, StatutCandidature statut,
                                                         Long filiereId, Long specialiteId){

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (sessionId != null){
                predicates.add(cb.equal(root.get("sessionCandidature").get("id"), sessionId));
            }
            if (statut != null){
                predicates.add(cb.equal(root.get("statutCandidature"), statut));
            }

            if (specialiteId != null){
                predicates.add(cb.equal(root.get("specialite").get("id"), specialiteId));
            }
            if (filiereId != null){
                predicates.add(cb.equal(root.get("specialite").get("filiere").get("id"), filiereId));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
