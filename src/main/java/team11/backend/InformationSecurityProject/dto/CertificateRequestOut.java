package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.CertificateRequest;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CertificateRequestOut {
    private Integer id;
    private Integer parentCertificateId;
    private String creationTime;
    private Boolean isAccepted;
    private String acceptanceTime;
    private Integer ownerId;
    private Integer linkedCertificateId;
    private String certificateType;

    public CertificateRequestOut(CertificateRequest certificateRequest){
        this.id = certificateRequest.getId();
        this.parentCertificateId = certificateRequest.getParent() == null ? null : certificateRequest.getParent().getId();
        this.creationTime = certificateRequest.getCreationTime().toString();
        this.isAccepted = certificateRequest.getIsAccepted();
        this.acceptanceTime = certificateRequest.getAcceptanceTime().toString();
        this.ownerId = certificateRequest.getOwner().getId();
        this.linkedCertificateId = certificateRequest.getLinkedCertificate() == null ? null : certificateRequest.getLinkedCertificate().getId();
        this.certificateType = certificateRequest.getCertificateType().toString();
    }
}
