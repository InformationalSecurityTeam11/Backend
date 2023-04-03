package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.model.RequestState;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CertificateRequestOut {
    private Integer id;
    private BigInteger parentCertificateSerialNumber;
    private String creationTime;
    private RequestState requestState;
    private String acceptanceTime;
    private Integer ownerId;
    private BigInteger linkedCertificateSerialNumber;
    private String certificateType;

    public CertificateRequestOut(CertificateRequest certificateRequest){
        this.id = certificateRequest.getId();
        this.parentCertificateSerialNumber = certificateRequest.getParent() == null ? null : certificateRequest.getParent().getSerialNumber();
        this.creationTime = certificateRequest.getCreationTime().toString();
        this.requestState = certificateRequest.getRequestState();
        this.acceptanceTime = certificateRequest.getAcceptanceTime() == null ? null : certificateRequest.getAcceptanceTime().toString();
        this.ownerId = certificateRequest.getOwner().getId();
        this.linkedCertificateSerialNumber = certificateRequest.getLinkedCertificate() == null ? null : certificateRequest.getLinkedCertificate().getSerialNumber();
        this.certificateType = certificateRequest.getCertificateType().toString();
    }
}
