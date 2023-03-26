package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findRoleByName(String roleName);
}
