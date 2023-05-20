package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.model.RequestState;
import team11.backend.InformationSecurityProject.model.User;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class RequestInfoDTO {

    private Integer id;
    private BigInteger parentCertificateSerialNumber;
    private String creationTime;
    private RequestState requestState;
    private String acceptanceTime;
    private UserOut owner;
    private String certificateType;
    private String rejection;
    private String organization;
    private String organizationUnit;

    public RequestInfoDTO(CertificateRequest certificateRequest, User user) {
        this.id = certificateRequest.getId();
        this.parentCertificateSerialNumber = certificateRequest.getParent() == null ? null : certificateRequest.getParent().getSerialNumber();
        this.creationTime = certificateRequest.getCreationTime().format(DateTimeFormatter.ISO_DATE_TIME);
        this.requestState = certificateRequest.getRequestState();
        this.acceptanceTime = certificateRequest.getAcceptanceTime() == null ? null : certificateRequest.getAcceptanceTime().format(DateTimeFormatter.ISO_DATE_TIME);
        this.certificateType = certificateRequest.getCertificateType().toString();
        this.rejection = certificateRequest.getRejection_reason();
        this.owner = new UserOut(user);
        this.organization = certificateRequest.getOrganization();
        this.organizationUnit = certificateRequest.getOrganizationUnit();

    }
}
