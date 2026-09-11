package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.RoleDTO;
import IFFPO_Web_Platform.entity.Permissions;
import IFFPO_Web_Platform.entity.Role;
import IFFPO_Web_Platform.repository.PermissionRepository;
import IFFPO_Web_Platform.repository.RoleRepository;
import IFFPO_Web_Platform.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Role> listerTous() {
        return roleRepository.findAll();
    }

    @Override
    public Role trouverParId(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Role non trouvé."));
    }

    @Override
    public Role creer(RoleDTO dto) {

        Role role = new Role();
        role.setIntitule(dto.getIntitule());
        role.setPermissions(resoudrePermissions(dto.getPermissionIds()));

        return roleRepository.save(role);
    }

    @Override
    public Role modifier(Long id, RoleDTO dto) {

        Role role = trouverParId(id);
        role.setIntitule(dto.getIntitule());
        role.setPermissions(resoudrePermissions(dto.getPermissionIds()));

        return roleRepository.save(role);
    }

    @Override
    public void supprimer(Long id) {
        roleRepository.deleteById(id);
    }

    private Set<Permissions> resoudrePermissions(List<Long> ids){

        if (ids == null || ids.isEmpty()){
            return new HashSet<>();
        }

        return new HashSet<>(permissionRepository.findAllById(ids));
    }



}
