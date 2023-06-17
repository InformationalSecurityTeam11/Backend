package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.model.CertificateRevoke;

public interface CertificateRevokeRepository extends JpaRepository<CertificateRevoke, Integer> {

}
