package team11.backend.InformationSecurityProject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.User;

@NoArgsConstructor
@Data
public class UserOut {
    private String userType;
    private String name;
    private String surname;
    private String email;
    private String telephoneNumber;
    private Integer id;

    public UserOut(User user){
        this.userType = user.getUserType();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.telephoneNumber = user.getTelephoneNumber();
        this.id = user.getId();
    }

}
