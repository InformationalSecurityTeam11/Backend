package team11.backend.InformationSecurityProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team11.backend.InformationSecurityProject.model.CertificateType;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CertificateInfoDTO {
    private LocalDateTime startDate;
    private UserInfoDTO userInfo;
    private CertificateType type;
}
