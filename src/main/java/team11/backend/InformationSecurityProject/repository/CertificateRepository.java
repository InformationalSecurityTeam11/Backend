package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.Certificate;
import org.springframework.stereotype.Repository;
import team11.backend.InformationSecurityProject.model.User;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    List<Certificate> findAll();
    Certificate getById(Integer id);
    Optional<Certificate> findCertificateBySerialNumber(BigInteger serial);
    List<Certificate> getCertificateByUser(User user);

}
