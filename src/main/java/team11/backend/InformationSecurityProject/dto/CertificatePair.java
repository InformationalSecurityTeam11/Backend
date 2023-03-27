package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import team11.backend.InformationSecurityProject.model.Certificate;

import java.security.cert.X509Certificate;

@NoArgsConstructor
@Data
public class CertificatePair {

    private Certificate certPreview;
    private X509Certificate certificate;
}
