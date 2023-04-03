package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.model.CertificateType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Getter
@Setter
public class ValidCertificateDTO {

    private Boolean valid;
    private LocalDate startDate;
    private LocalDate expiredDate;

    public ValidCertificateDTO(Certificate certificate){
        this.valid = true;
        this.startDate = certificate.getStartDate().toLocalDate();
        this.expiredDate = certificate.getExpireDate().toLocalDate();
    }
    public ValidCertificateDTO(Boolean valid){
        this.valid = valid;
        this.startDate = null;
        this.expiredDate = null;
    }
}
