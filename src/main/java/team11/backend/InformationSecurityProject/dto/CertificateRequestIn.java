package team11.backend.InformationSecurityProject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.CertificateType;

@Data
@NoArgsConstructor
public class CertificateRequestIn {
    private Integer parentCertificateId;
    @NotNull(message = "Field (certificateType) is required")
    private CertificateType certificateType;
    private String organization;
    private String organizationUnit;

}
