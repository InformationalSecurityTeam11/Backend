package team11.backend.InformationSecurityProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoDTO {
    private String name;
    private String surname;
    private String telephoneNumber;
    private String email;

}
