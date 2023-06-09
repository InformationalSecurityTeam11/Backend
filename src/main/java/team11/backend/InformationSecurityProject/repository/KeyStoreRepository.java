package team11.backend.InformationSecurityProject.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class KeyStoreRepository {
    private final KeyStore keyStore;

    @Value("${app.keystore-password}")
    private String SECRET;
    private final String KEYSTORE_FILENAME = "./src/main/java/team11/backend/InformationSecurityProject/cert/keystore.jks";
    private boolean initialize = true;
    public KeyStoreRepository(){
        try {
            this.keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey(BigInteger certificateSerial){
        try {
            loadKeyStore();
            return (PrivateKey) keyStore.getKey(getKeyAliasFromSerial(certificateSerial), SECRET.toCharArray());
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Certificate> getCertificate(BigInteger certificateID){
        loadKeyStore();
        String alias = getCertificateAliasFromSerial(certificateID);
        try {
            if(keyStore.isCertificateEntry(alias)){
                Certificate certificate = keyStore.getCertificate(alias);
                return Optional.of(certificate);
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }




    public void loadKeyStore(){
        if(initialize){
            try {
                keyStore.load(null, SECRET.toCharArray());
            } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                keyStore.load(new FileInputStream(KEYSTORE_FILENAME), SECRET.toCharArray());
            } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getKeyAliasFromSerial(BigInteger id){
        return "PRIVATE_KEY_" + id;
    }
    public String getCertificateAliasFromSerial(BigInteger id){
        return "CERTIFICATE_" + id;
    }

    private void saveKeyStore(){
        try {
            if(initialize){
                File file = new File(KEYSTORE_FILENAME);
                file.delete();
                initialize = false;
            }
            keyStore.store(new FileOutputStream(KEYSTORE_FILENAME), SECRET.toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCertificate(Certificate certificate, BigInteger certificateSerial){
        loadKeyStore();
        try {
            keyStore.setCertificateEntry(getCertificateAliasFromSerial(certificateSerial), certificate);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        saveKeyStore();
    }
    public void savePrivateKey(BigInteger certificateSerial, PrivateKey privateKey){
        loadKeyStore();
        Optional<Certificate> certificateOpt = getCertificate(certificateSerial);
        if(certificateOpt.isEmpty()){
            throw new RuntimeException("Certificate associated with certificateSerial not found");
        }
        try {
            keyStore.setKeyEntry(getKeyAliasFromSerial(certificateSerial),
                    privateKey,
                    SECRET.toCharArray(),
                    new Certificate[]{certificateOpt.get()});
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        saveKeyStore();
    }

    public List<X509Certificate> getCertificatesSignedBy(BigInteger certificateSerial) {
        loadKeyStore();
        String alias = getCertificateAliasFromSerial(certificateSerial);
        List<X509Certificate> signedCertificates = new ArrayList<>();

        try {
            if (keyStore.isCertificateEntry(alias)) {
                Certificate certificate = keyStore.getCertificate(alias);
                signedCertificates.addAll(getChildCertificates(certificate));
                if(signedCertificates.contains(certificate)){
                    signedCertificates.remove(certificate);
                    signedCertificates.remove(certificate);
                }
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        return signedCertificates;
    }
    private List<X509Certificate> getChildCertificates(Certificate parentCertificate) {
        List<X509Certificate> childCertificates = new ArrayList<>();

        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate certificate = keyStore.getCertificate(alias);

                if (certificate instanceof X509Certificate) {
                    X509Certificate x509Certificate = (X509Certificate) certificate;

                    if (isCertificateSignedBy(x509Certificate, parentCertificate)) {
                        childCertificates.add(x509Certificate);
                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        return childCertificates;
    }

    private boolean isCertificateSignedBy(X509Certificate certificate, Certificate parentCertificate) {
        try {
            certificate.verify(parentCertificate.getPublicKey());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
