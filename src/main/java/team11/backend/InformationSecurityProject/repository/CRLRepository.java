package team11.backend.InformationSecurityProject.repository;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CRLEntryHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import team11.backend.InformationSecurityProject.utils.CertificateUtility;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.*;
import java.time.Instant;
import java.util.*;

@Repository
public class CRLRepository {
    private X509CRL crl;
    private String CRL_FILENAME = "./src/main/java/team11/backend/InformationSecurityProject/cert/revoked.crl";
    private boolean initialize = true;
    private KeyStoreRepository keyStoreRepository;

    @Autowired
    public CRLRepository(KeyStoreRepository keyStoreRepository) {
        this.keyStoreRepository = keyStoreRepository;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            FileInputStream crlFile = new FileInputStream(CRL_FILENAME);
            this.crl = (X509CRL) cf.generateCRL(crlFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (CRLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRevoked(X509Certificate certificate) {
        try {
            return crl.isRevoked(certificate);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setCertificateRevocationList(X509CRL crl) {
        this.crl = crl;
    }

    public void revokeCertificate(X509Certificate certificate) {
        crl = addCertificateToCRL(certificate, crl);
        revokeSignedCertificates(certificate);
    }

    private void revokeSignedCertificates(X509Certificate certificate) {
        List<X509Certificate> signedCertificates = getSignedCertificates(certificate);
        for (X509Certificate signedCertificate : signedCertificates) {
            crl = addCertificateToCRL(signedCertificate, crl);
            revokeSignedCertificates(signedCertificate);
        }
    }

    private List<X509Certificate> getSignedCertificates(X509Certificate certificate) {
        List<X509Certificate> signedCertificates = new ArrayList<>();
        signedCertificates = keyStoreRepository.getCertificatesSignedBy(certificate.getSerialNumber());
        return signedCertificates;
    }

    private X509CRL addCertificateToCRL(X509Certificate certificate, X509CRL existingCrl){
        try {
            KeyPair pair =  CertificateUtility.generateKeyPair();
            PrivateKey privateKey  = pair.getPrivate();
            X509CRLHolder crlHolder = new X509CRLHolder(existingCrl.getEncoded());

            X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(crlHolder);

            // Create CRLEntry
            BigInteger serialNumber = certificate.getSerialNumber();
            Date revocationDate = Date.from(Instant.now());
            crlBuilder.addCRLEntry(serialNumber, revocationDate, CRLReason.PRIVILEGE_WITHDRAWN.ordinal());

            // Set nextUpdate date and other necessary information
            crlBuilder.setNextUpdate(Date.from(Instant.now().plusSeconds(86400))); // Set the nextUpdate date (e.g., 24 hours from now)

            // Sign the CRL
            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
            X509CRLHolder newCrlHolder = crlBuilder.build(contentSigner);

            // Convert the new CRL holder to X509CRL
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            byte[] encodedCrl = newCrlHolder.getEncoded();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(encodedCrl);
            X509CRL newCrl = (X509CRL) cf.generateCRL(inputStream);

            return newCrl;
        } catch (CertificateException | CRLException | IOException | OperatorCreationException e) {
            throw new RuntimeException("Failed to add the certificate to CRL.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }
}