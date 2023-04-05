package team11.backend.InformationSecurityProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.model.CertificateType;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class RequestDTO {
    private Integer id;
    private CertificateInfoDTO certificateParent;
    private LocalDateTime creationTime;
    private String requestState;
    private LocalDateTime acceptanceTime;
    private UserInfoDTO owner;
    private CertificateType certificateType;
    private String organization;
    private String organizationUnit;

    public RequestDTO(CertificateRequest request) {
        this.id = request.getId();
        this.certificateParent = new CertificateInfoDTO(request.getParent());
        this.creationTime = request.getCreationTime();
        this.requestState = request.getRequestState().toString();
        this.owner = new UserInfoDTO(request.getOwner());
        this.certificateType = request.getCertificateType();
        this.organization = request.getOrganization();
        this.organizationUnit = request.getOrganizationUnit();

    }
}
