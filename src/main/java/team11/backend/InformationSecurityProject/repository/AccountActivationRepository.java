package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.AccountActivation;

public interface AccountActivationRepository extends JpaRepository<AccountActivation, Integer> {
}
