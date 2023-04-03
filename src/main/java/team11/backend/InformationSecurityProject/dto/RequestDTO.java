package team11.backend.InformationSecurityProject.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.model.CertificateType;
import team11.backend.InformationSecurityProject.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor

public class RequestDTO {
    private Integer id;
    private CertificateInfoDTO certificateParent;
    private LocalDateTime creationTime;
    private Boolean isAccepted;
    private LocalDateTime acceptanceTime;
    private UserInfoDTO owner;
    private CertificateType certificateType;
    private String organization;
    private String organizationUnit;

    public RequestDTO(CertificateRequest request) {
        this.id = request.getId();
        this.certificateParent = new CertificateInfoDTO(request.getParent());
        this.creationTime = request.getCreationTime();
        this.isAccepted = request.getIsAccepted();
        this.owner = new UserInfoDTO(request.getOwner());
        this.certificateType = request.getCertificateType();
        this.organization = request.getOrganization();
        this.organizationUnit = request.getOrganizationUnit();

    }
}
