package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.RoleDTO;
import IFFPO_Web_Platform.entity.Role;
import IFFPO_Web_Platform.repository.PermissionRepository;
import IFFPO_Web_Platform.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard/roles")
@PreAuthorize("hasAuthority('PERM_GESTION_ROLES')")
public class RoleController {

    private final RoleService roleService;
    private final PermissionRepository permissionRepository;

    public RoleController(RoleService roleService, PermissionRepository permissionRepository) {
        this.roleService = roleService;
        this.permissionRepository = permissionRepository;
    }

    @GetMapping()
    public String liste(Model model){

        model.addAttribute("roles", roleService.listerTous());
        model.addAttribute("roleDTO", new RoleDTO());
        model.addAttribute("AllsPermissions", permissionRepository.findAll());

        return "admin/roles/liste";
    }

    @GetMapping("/nouveau")
    public String formCreate(Model model){
        model.addAttribute("roleDTO", new RoleDTO());
        model.addAttribute("AllsPermissions", permissionRepository.findAll());
        return "admin/roles/formulaire";
    }

    @PostMapping("/nouveau")
    public String createRole(@ModelAttribute("roleDTO") RoleDTO dto){
        roleService.creer(dto);

        return "redirect:/dashboard/roles";
    }

    @GetMapping("/{id}/modifier")
    public String formUpdate(@PathVariable Long id, Model model){

        Role role = roleService.trouverParId(id);

        RoleDTO roleDTO = new RoleDTO();

        roleDTO.setId(role.getId());
        roleDTO.setIntitule(role.getIntitule());
        roleDTO.setPermissionIds(role.getPermissions().stream()
                .map(p->p.getId())
                .collect(Collectors.toList()));

        model.addAttribute("roleDTO", roleDTO);
        model.addAttribute("AllsPermissions", permissionRepository.findAll());

        return "admin/roles/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String update(@PathVariable Long id, @ModelAttribute("roleDTO") RoleDTO dto){

        roleService.modifier(id, dto);
        return "redirect:/dashboard/roles";
    }

    @PostMapping("/{id}/supprimer")
    public String delete(@PathVariable Long id){
        roleService.supprimer(id);
        return "redirect:/dashboard/roles";
    }

}
