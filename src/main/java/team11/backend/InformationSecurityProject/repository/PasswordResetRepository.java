package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.PasswordReset;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer> {
}
