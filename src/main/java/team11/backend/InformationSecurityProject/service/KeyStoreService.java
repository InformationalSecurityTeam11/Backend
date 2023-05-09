package team11.backend.InformationSecurityProject.service;

import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.repository.KeyStoreRepository;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@Service
public class KeyStoreService {

    private final KeyStoreRepository keyStoreRepository;

    public KeyStoreService(KeyStoreRepository keyStoreRepository) {
        this.keyStoreRepository = keyStoreRepository;
    }

    public Certificate getCertificate(BigInteger certificateID){
        return this.keyStoreRepository.getCertificate(certificateID).orElse(null);
    }


    public KeyStore createKeyStore(String password) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        // Create a new empty keystore instance
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // Initialize the keystore with a null input stream and password
        keyStore.load(null, password.toCharArray());

        return keyStore;
    }

    public KeyStore loadKeyStore(File file, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(file)) {
            keyStore.load(fis, password.toCharArray());
        }
        return keyStore;
    }

    public void saveKeyStore(KeyStore keystore, File file, String password) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        try (OutputStream out = new FileOutputStream(file)) {
            keystore.store(out, password.toCharArray());
        }
    }

    public void addPrivateKeyToKeyStore(KeyStore keystore, String alias, PrivateKey privateKey, Certificate[] chain, String password) throws Exception {
        keystore.setKeyEntry(alias, privateKey, password.toCharArray(), chain);
    }

    public PrivateKey getPrivateKeyFromKeyStore(KeyStore keyStore, String alias, String password) throws Exception {
        if (!keyStore.containsAlias(alias)) {
            throw new Exception("Alias not found in keystore");
        }

        Key key = keyStore.getKey(alias, password.toCharArray());
        if (!(key instanceof PrivateKey)) {
            throw new Exception("Alias does not contain a private key");
        }

        return (PrivateKey) key;
    }

    public void addCertificateToKeyStore(KeyStore keystore, String alias, X509Certificate certificate) throws KeyStoreException {
        keystore.setCertificateEntry(alias, certificate);
    }

    public X509Certificate getCertificateFromKeyStore(KeyStore keystore, String alias) throws Exception {
        if (!keystore.containsAlias(alias)) {
            throw new Exception("Certificate with alias " + alias + " not found in keystore");
        }

        return (X509Certificate) keystore.getCertificate(alias);
    }

    public void deleteEntryFromKeyStore(KeyStore keyStore, String alias) throws KeyStoreException {
        keyStore.deleteEntry(alias);
    }

    public List<String> getKeyStoreAliases(KeyStore keystore) throws KeyStoreException {
        List<String> aliases = new ArrayList<>();
        Enumeration<String> enumeration = keystore.aliases();
        while (enumeration.hasMoreElements()) {
            String alias = enumeration.nextElement();
            aliases.add(alias);
        }
        return aliases;
    }

    public List<X509Certificate> getKeyStoreCertificateChain(KeyStore keystore, String alias) throws KeyStoreException, CertificateException {
        Certificate[] certs = keystore.getCertificateChain(alias);
        if (certs == null) {
            throw new CertificateException("No certificate chain found for alias: " + alias);
        }
        List<X509Certificate> chain = new ArrayList<>();
        for (Certificate cert : certs) {
            if (cert instanceof X509Certificate) {
                chain.add((X509Certificate) cert);
            }
        }
        return chain;
    }

}
