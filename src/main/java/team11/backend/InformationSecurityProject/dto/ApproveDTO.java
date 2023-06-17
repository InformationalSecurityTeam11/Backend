package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Data
@Getter
@Setter
public class ApproveDTO {
    private int id;

    public ApproveDTO(int id) {
        this.id = id;
    }
}
