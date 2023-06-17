package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
