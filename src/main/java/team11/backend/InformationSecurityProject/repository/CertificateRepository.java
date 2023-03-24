package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
}
