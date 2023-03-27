package team11.backend.InformationSecurityProject.service;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.CertificateInfoDTO;
import team11.backend.InformationSecurityProject.dto.CertificatePair;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.model.CertificateType;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.repository.CertificateRepository;
import team11.backend.InformationSecurityProject.repository.KeyStoreRepository;
import team11.backend.InformationSecurityProject.service.interfaces.CertificatePreviewService;
import team11.backend.InformationSecurityProject.service.interfaces.CertificateRequestService;
import team11.backend.InformationSecurityProject.service.interfaces.ICertificateService;
import team11.backend.InformationSecurityProject.utils.CertificateUtility;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CertificateService implements ICertificateService {

    private final CertificateRepository certificateRepository;
    private final KeyStoreRepository keyStoreRepository;
    private final CertificateUtility certificateUtility;
    private final CertificatePreviewService certificatePreviewService;

    public CertificateService(CertificateRepository certificateRepository, KeyStoreRepository keyStoreRepository, CertificateUtility certificateUtility, CertificatePreviewService certificatePreviewService) {
        this.certificateRepository = certificateRepository;
        this.keyStoreRepository = keyStoreRepository;
        this.certificateUtility = certificateUtility;
        this.certificatePreviewService = certificatePreviewService;
    }

    /**
     * Creates a self-signed X.509 certificate with the given key pair, subject name and validity period.
     *
     * @param keyPair the key pair to use for the certificate
     * @param subject the subject name for the certificate
     * @param days the number of days the certificate is valid for
     * @return a self-signed X.509 certificate
     * @throws Exception if an error occurs while creating the certificate
     */
    public X509Certificate createSelfSignedCertificate(KeyPair keyPair, X500Name subject, int days) throws Exception {
        // Generate a serial number based on timestamp
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(subject, serialNumber, new Date(), new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days)), subject, keyPair.getPublic());

        // Add the basic constraints extension to make it a CA certificate
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        // Add the subject key identifier extension
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

        // Sign the certificate
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(signer);
        return new JcaX509CertificateConverter().getCertificate(certHolder);
    }
    /**
     * Creates a new X.509 certificate signed by the provided CA certificate and private key,
     * using the provided key pair and subject information. The certificate is valid for the
     * specified number of days and has the given serial number. The function returns the
     * newly created certificate as an X509Certificate object.
     *
     * @param parentCertificateID the id of the Certificate Authority (CA) that will sign the certificate
     * @param subject             the subject name of the entity requesting the certificate
     * @param days                the number of days that the certificate will be valid
     * @param owner               user requesting the certificate
     * @param type                type of requested certificate
     * @return the newly created X509Certificate object
     * @throws Exception if there is an error creating the certificate
     */
    @Override
    public CertificatePair createCertificate(Integer parentCertificateID, X500Name subject, int days, User owner, CertificateType type) throws Exception {
        Optional<java.security.cert.Certificate> caCertOpt = keyStoreRepository.getCertificate(parentCertificateID);
        if(caCertOpt.isEmpty()){
            throw new RuntimeException("Certificate associated with parentCertificateID not found");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDate = now.plusDays(days);

        Certificate certificate = new Certificate();
        certificate.setUser(owner);
        certificate.setType(type);
        certificate.setStartDate(now);
        certificate.setExpireDate(expireDate);
        Certificate certSaved = certificatePreviewService.insert(certificate);

        X509Certificate caCert = (X509Certificate) caCertOpt.get();
        PrivateKey caPrivateKey = keyStoreRepository.getPrivateKey(parentCertificateID);

        Date nowDateLegacy = java.sql.Timestamp.valueOf(now);
        Date expireDateLegacy = java.sql.Timestamp.valueOf(expireDate);

        KeyPair keyPairIssuer = certificateUtility.generateKeyPair();
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(caCert, BigInteger.valueOf(certSaved.getId()), nowDateLegacy, expireDateLegacy, subject, keyPairIssuer.getPublic());

        // Add the basic constraints extension to make it a non-CA certificate
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        // Add the subject key identifier extension
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(keyPairIssuer.getPublic()));

        // Sign the certificate
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(caPrivateKey);
        X509CertificateHolder certHolder = certBuilder.build(signer);

        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

        keyStoreRepository.saveCertificate(cert, certSaved.getId());

        CertificatePair output = new CertificatePair();
        output.setCertificate(cert);
        output.setCertPreview(certificate);

        return output;
    }

    /**

     Validates the given X.509 certificate.
     @param certificate the certificate to be validated
     @param expectedSigner the public key of the expected signer of the certificate
     */
    public boolean validateCertificate(X509Certificate certificate, X509Certificate expectedSigner) throws Exception {
        // Check if the certificate is expired
        certificate.checkValidity();

        // Check if the certificate has been revoked
        if (isCertificateRevoked(certificate)) {
            throw new Exception("Certificate has been revoked");
        }

        // Check if the certificate is signed by the expected signer
        PublicKey expectedSignerPublicKey = expectedSigner.getPublicKey();
        certificate.verify(expectedSignerPublicKey);

        return true;
    }

    @Override
    public List<Certificate> getAll() {
        return this.certificateRepository.findAll();
    }

    @Override
    public Set<CertificateInfoDTO> getCertificatesDTOS(List<Certificate> certificates) {
        if(certificates.size() == 0) return null;
        Set<CertificateInfoDTO> certificateInfoDTOS = new HashSet<>();
        for(Certificate certificate : certificates){
            certificateInfoDTOS.add(new CertificateInfoDTO(certificate));
        }
        return certificateInfoDTOS;

    }

    private boolean isCertificateRevoked(X509Certificate certificate) {
        // TODO: Implement certificate revocation check logic
        // This could involve checking a Certificate Revocation List (CRL),
        // an Online Certificate Status Protocol (OCSP) server, or some other method.
        // For simplicity, we will assume that the certificate is not revoked.
        return false;
    }

}
