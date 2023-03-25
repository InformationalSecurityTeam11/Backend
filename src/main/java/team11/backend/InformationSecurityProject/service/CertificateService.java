package team11.backend.InformationSecurityProject.service;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Component;
import team11.backend.InformationSecurityProject.service.interfaces.ICertificateService;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class CertificateService implements ICertificateService {

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
     * @param caCert the X.509 certificate of the Certificate Authority (CA) that will sign the certificate
     * @param caPrivateKey the private key of the CA used to sign the certificate
     * @param keyPair the key pair of the entity requesting the certificate
     * @param subject the subject name of the entity requesting the certificate
     * @param days the number of days that the certificate will be valid
     * @param serial the serial number of the certificate
     * @return the newly created X509Certificate object
     * @throws Exception if there is an error creating the certificate
     */
    public X509Certificate createCertificate(X509Certificate caCert, PrivateKey caPrivateKey, KeyPair keyPair, X500Name subject, int days, BigInteger serial) throws Exception {
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(caCert, serial, new Date(), new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days)), subject, keyPair.getPublic());

        // Add the basic constraints extension to make it a non-CA certificate
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        // Add the subject key identifier extension
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

        // Sign the certificate
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(caPrivateKey);
        X509CertificateHolder certHolder = certBuilder.build(signer);
        return new JcaX509CertificateConverter().getCertificate(certHolder);
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

    private boolean isCertificateRevoked(X509Certificate certificate) {
        // TODO: Implement certificate revocation check logic
        // This could involve checking a Certificate Revocation List (CRL),
        // an Online Certificate Status Protocol (OCSP) server, or some other method.
        // For simplicity, we will assume that the certificate is not revoked.
        return false;
    }

}
