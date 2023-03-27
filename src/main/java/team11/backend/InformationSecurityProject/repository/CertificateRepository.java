package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.Certificate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    List<Certificate> findAll();

    Optional<Certificate> findCertificateBySerialNumber(BigInteger serial);
}
