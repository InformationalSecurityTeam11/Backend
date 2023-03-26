package team11.backend.InformationSecurityProject.service;

import team11.backend.InformationSecurityProject.model.Role;

public interface RoleService {
    Role findRoleByName(String role);
}
