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

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.time.Instant;
import java.util.*;

@Repository
public class CRLRepository {
    private X509CRL crl;
    private String CRL_FILENAME = "./src/main/java/team11/backend/InformationSecurityProject/cert/revoked.crl";
    private boolean initialized = false;
    public CRLRepository() {
        this.initialized = false;
    }

    public X509CRL getCRL() {
        if (crl == null) {
            loadCRL();
        }
        return crl;
    }

    private void saveCRL() throws IOException, CRLException {
        FileOutputStream fos = new FileOutputStream(CRL_FILENAME);
        fos.write(crl.getEncoded());
        fos.close();
    }


    public void loadCRL() {
        if (this.initialized) {
            try {
                crl = generateCRLFromFile();
                this.crl = crl;
                saveCRL();
            } catch (IOException | CertificateException | CRLException e) {
                throw new RuntimeException("Failed to load CRL from file.", e);
            }
        } else {
            try {
                crl = generateNewCRL();
                this.crl = crl;
                saveCRL();
                this.initialized = true;
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate a new CRL.", e);
            }
        }
    }

    private X509CRL generateNewCRL() {
        try {
            KeyPair keyPair = CertificateUtility.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(new X500Name("CN=Issuer Common Name, OU=Issuer Organizational Unit, O=Issuer Organization, L=Issuer Locality, ST=Issuer State, C=Issuer Country"), new Date());
            crlBuilder.setNextUpdate(Date.from(Instant.now().plusSeconds(86400))); // Set the nextUpdate date (e.g., 24 hours from now)
            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
            X509CRLHolder crlHolder = crlBuilder.build(contentSigner);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            byte[] encodedCrl = crlHolder.getEncoded();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(encodedCrl);
            X509CRL newCrl = (X509CRL) cf.generateCRL(inputStream);
            return newCrl;
        } catch (CertificateException | CRLException | IOException | OperatorCreationException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate the new CRL.", e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    private X509CRL generateCRLFromFile() throws IOException, CertificateException, CRLException {
        FileInputStream fis = new FileInputStream(CRL_FILENAME);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        crl = (X509CRL) cf.generateCRL(fis);
        fis.close();
        return crl;
    }

    public boolean isRevoked(BigInteger serialNumber) {
        this.loadCRL();
        try {
            X509CRLEntry cert =  crl.getRevokedCertificate(serialNumber);
            if(cert == null) return false;
            else return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void addCertificateToCRL(X509Certificate certificate){
        this.loadCRL();
        try {
            KeyPair pair =  CertificateUtility.generateKeyPair();
            PrivateKey privateKey  = pair.getPrivate();
            X509CRLHolder crlHolder = new X509CRLHolder(crl.getEncoded());

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
            crl =  newCrl;
            saveCRL();
        } catch (CertificateException | CRLException | IOException | OperatorCreationException e) {
            throw new RuntimeException("Failed to add the certificate to CRL.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public void printCRLContents() {
        Set<? extends X509CRLEntry> entries = crl.getRevokedCertificates();
        if (entries != null) {
            for (X509CRLEntry entry : entries) {
                X500Principal certificateIssuer = crl.getIssuerX500Principal();
                BigInteger serialNumber = entry.getSerialNumber();
                Date revocationDate = entry.getRevocationDate();
                CRLReason reasonCode = entry.getRevocationReason();

                System.out.println("Issuer: " + certificateIssuer);
                System.out.println("Serial Number: " + serialNumber);
                System.out.println("Revocation Date: " + revocationDate);
                System.out.println("Reason Code: " + reasonCode);
                System.out.println();
            }
        } else {
            System.out.println("No revoked certificates found in the CRL.");
        }
    }

}