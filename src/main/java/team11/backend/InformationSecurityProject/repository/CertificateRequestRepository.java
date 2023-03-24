package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.CertificateRequest;

public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Integer> {
}
