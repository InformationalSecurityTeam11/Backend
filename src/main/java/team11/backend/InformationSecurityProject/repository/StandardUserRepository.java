package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.StandardUser;

public interface StandardUserRepository extends JpaRepository<StandardUser, Integer> {
}
