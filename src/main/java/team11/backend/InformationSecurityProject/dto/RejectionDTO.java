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
public class RejectionDTO {
    private int id;
    private String reason;

    public RejectionDTO(int id, String reason) {
        this.id = id;
        this.reason = reason;
    }
}
