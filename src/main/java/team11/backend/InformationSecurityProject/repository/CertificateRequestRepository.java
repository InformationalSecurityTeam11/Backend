package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.model.User;

import java.util.List;

public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Integer> {
    List<CertificateRequest> getCertificateRequestByOwner(User owner);
}
