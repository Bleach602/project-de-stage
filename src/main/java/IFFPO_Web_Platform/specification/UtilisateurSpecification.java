package IFFPO_Web_Platform.specification;

import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.StatutCompte;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UtilisateurSpecification {

    public static Specification<Utilisateur> avecFiltres(String categorie, Long roleId,
                                                         StatutCompte statutCompte,
                                                         String recherche){

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if ("administratifs".equals(categorie)){
                predicates.add(cb.notEqual(root.get("role").get("intitule"), "ROLE_CANDIDAT"));
            } else if ("candidats".equals(categorie)) {
                predicates.add(cb.equal(root.get("role").get("intitule"), "ROLE_CANDIDAT"));
            }

            if (roleId != null){
                predicates.add(cb.equal(root.get("role").get("id"), roleId));
            }

            if (statutCompte != null){
                predicates.add(cb.equal(root.get("statutCompte"), statutCompte));
            }

            if (recherche != null && !recherche.isBlank()){
                predicates.add(cb.like(cb.lower(root.get("nom")), "%" +
                        recherche.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
