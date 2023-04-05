package team11.backend.InformationSecurityProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.model.CertificateType;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CertificateInfoDTO {
    private UserInfoDTO ownerInfo;
    private String startDate;
    private String expireDate;
    private CertificateType type;
    private BigInteger serialNumber;
    public CertificateInfoDTO(Certificate certificate) {
        this.ownerInfo = new UserInfoDTO(certificate.getUser());
        this.startDate = certificate.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME);
        this.expireDate = certificate.getExpireDate().format(DateTimeFormatter.ISO_DATE_TIME);
        this.serialNumber = certificate.getSerialNumber();
        this.type = certificate.getType();
    }
}
