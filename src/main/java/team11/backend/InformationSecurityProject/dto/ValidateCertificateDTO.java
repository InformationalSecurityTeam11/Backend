package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@NoArgsConstructor
@Data
@Getter
@Setter
public class ValidateCertificateDTO {
    private BigInteger serialNumber;

    public ValidateCertificateDTO(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

}
