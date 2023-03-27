package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.model.Role;
import team11.backend.InformationSecurityProject.repository.RoleRepository;
import team11.backend.InformationSecurityProject.service.interfaces.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository){

        this.roleRepository = roleRepository;
    }
    @Override
    public Role findRoleByName(String role) {
        return roleRepository.findRoleByName(role);
    }
}
