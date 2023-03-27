package team11.backend.InformationSecurityProject.repository;

import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Optional;

@Repository
public class KeyStoreRepository {
    private final KeyStore keyStore;
    private final String SECRET = "default_keystore_password";
    private final String KEYSTORE_FILENAME = "./";
    private final boolean INITIALIZE = true;
    public KeyStoreRepository(){
        try {
            this.keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey(Integer certificateID){
        try {
            loadKeyStore();
            return (PrivateKey) keyStore.getKey(getKeyAliasFromID(certificateID), null);
        } catch (NoSuchAlgorithmException | KeyStoreException |
                 UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Certificate> getCertificate(Integer certificateID){
        loadKeyStore();
        String alias = getCertificateAliasFromID(certificateID);
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

    private void loadKeyStore(){
        if(INITIALIZE){
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

    private String getKeyAliasFromID(Integer id){
        return "PRIVATE_KEY_" + id;
    }
    private String getCertificateAliasFromID(Integer id){
        return "CERTIFICATE_" + id;
    }

    private void saveKeyStore(){
        try {
            keyStore.store(new FileOutputStream(KEYSTORE_FILENAME), SECRET.toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCertificate(Certificate certificate, Integer certificateID){
        loadKeyStore();
        try {
            keyStore.setCertificateEntry(getCertificateAliasFromID(certificateID), certificate);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        saveKeyStore();
    }
    public void savePrivateKey(Integer certificateID, PrivateKey privateKey){
        loadKeyStore();
        Optional<Certificate> certificateOpt = getCertificate(certificateID);
        if(certificateOpt.isEmpty()){
            throw new RuntimeException("Certificate associated with certificateID is not found");
        }
        try {
            keyStore.setKeyEntry(getKeyAliasFromID(certificateID),
                    privateKey,
                    SECRET.toCharArray(),
                    new Certificate[]{certificateOpt.get()});
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        saveKeyStore();
    }
}
