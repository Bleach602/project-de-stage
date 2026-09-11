package IFFPO_Web_Platform.service;

import IFFPO_Web_Platform.dto.RoleDTO;
import IFFPO_Web_Platform.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> listerTous();
    Role trouverParId(Long id);
    Role creer(RoleDTO dto);
    Role modifier(Long id, RoleDTO dto);
    void supprimer(Long id);
}
