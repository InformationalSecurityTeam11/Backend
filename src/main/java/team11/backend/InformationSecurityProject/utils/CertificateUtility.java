package team11.backend.InformationSecurityProject.utils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import team11.backend.InformationSecurityProject.dto.SubjectInfoDTO;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class CertificateUtility {

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    public static X500Name generateX500Name(SubjectInfoDTO subjectInfo) {
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
