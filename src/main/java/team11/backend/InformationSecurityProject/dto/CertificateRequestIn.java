package team11.backend.InformationSecurityProject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.CertificateType;

import java.math.BigInteger;

@Data
@NoArgsConstructor
public class CertificateRequestIn {
    private BigInteger parentCertificateSerialNumber;
    @NotNull(message = "Field (certificateType) is required")
    private CertificateType certificateType;
    private String organization;
    private String organizationUnit;

}
