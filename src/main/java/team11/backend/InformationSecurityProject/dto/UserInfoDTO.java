package team11.backend.InformationSecurityProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team11.backend.InformationSecurityProject.model.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoDTO {
    private String name;
    private String surname;
    private String telephoneNumber;
    private String email;

    public UserInfoDTO(User user) {
        this.name = user.getName();
        this.surname = user.getSurname();
        this.telephoneNumber = user.getTelephoneNumber();
        this.email = user.getEmail();
    }
}
