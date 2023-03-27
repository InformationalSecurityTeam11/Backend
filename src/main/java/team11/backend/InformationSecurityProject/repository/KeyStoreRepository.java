package team11.backend.InformationSecurityProject.repository;

import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Optional;

@Repository
public class KeyStoreRepository {
    private final KeyStore keyStore;
    private final String SECRET = "changeit";
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
            PrivateKey key = (PrivateKey) keyStore.getKey(getKeyAliasFromSerial(certificateSerial), SECRET.toCharArray());
            return key;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            throw new RuntimeException(e);
        }catch (KeyStoreException e){
            System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
            throw new RuntimeException(e);
        }catch (UnrecoverableKeyException e){
            System.out.println("ccccccccccccccccccccccccccccccc");
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

    private void loadKeyStore(){
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

    private String getKeyAliasFromSerial(BigInteger id){
        return "PRIVATE_KEY_" + id;
    }
    private String getCertificateAliasFromSerial(BigInteger id){
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
}
