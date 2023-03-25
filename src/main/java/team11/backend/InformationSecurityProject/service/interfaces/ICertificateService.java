package team11.backend.InformationSecurityProject.service.interfaces;

import org.bouncycastle.asn1.x500.X500Name;
import team11.backend.InformationSecurityProject.dto.CertificateInfoDTO;
import team11.backend.InformationSecurityProject.model.Certificate;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ICertificateService {
    X509Certificate createSelfSignedCertificate(KeyPair keyPair, X500Name subject, int days) throws Exception;
    X509Certificate createCertificate(X509Certificate caCert, PrivateKey caPrivateKey, KeyPair keyPair, X500Name subject, int days, BigInteger serial) throws Exception;
    boolean validateCertificate(X509Certificate certificate, X509Certificate expectedSigner) throws Exception;
    List<Certificate> getAll();

    Set<CertificateInfoDTO> getCertificatesDTOS(List<Certificate> certificates);
}
