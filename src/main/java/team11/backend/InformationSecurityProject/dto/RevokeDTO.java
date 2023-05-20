package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@Getter
@Setter
public class RevokeDTO {
    private BigInteger serialNumber;
    private String reason;
}
