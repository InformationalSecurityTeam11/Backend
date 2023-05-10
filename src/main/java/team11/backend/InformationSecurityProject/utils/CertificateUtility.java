package team11.backend.InformationSecurityProject.utils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.SubjectInfoDTO;

import java.security.*;

@Service
public class CertificateUtility {

    public CertificateUtility(){

    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyPairGenerator.initialize(2048, random);
        return keyPairGenerator.generateKeyPair();
    }

    public X500Name generateX500Name(SubjectInfoDTO subjectInfo) {
        // Create a new X500NameBuilder and set the required fields
        X500NameBuilder builder = new X500NameBuilder(RFC4519Style.INSTANCE)
                .addRDN(BCStyle.CN, subjectInfo.getCommonName())
                .addRDN(BCStyle.O, subjectInfo.getOrganizationName())
                .addRDN(BCStyle.OU, subjectInfo.getOrganizationUnit());

        // Add optional fields if they are not null or empty
        if (subjectInfo.getStateName() != null && !subjectInfo.getStateName().isEmpty()) {
            builder.addRDN(BCStyle.ST, subjectInfo.getStateName());
        }
        if (subjectInfo.getLocalityName() != null && !subjectInfo.getLocalityName().isEmpty()) {
            builder.addRDN(BCStyle.L, subjectInfo.getLocalityName());
        }
        if (subjectInfo.getEmail() != null && !subjectInfo.getEmail().isEmpty()) {
            builder.addRDN(BCStyle.E, subjectInfo.getEmail());
        }

        // Return the X500Name object
        return builder.build();
    }
}
