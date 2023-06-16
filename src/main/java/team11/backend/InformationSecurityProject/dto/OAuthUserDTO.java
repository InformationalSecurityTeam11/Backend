package team11.backend.InformationSecurityProject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class OAuthUserDTO {

    private String name;
    private String surname;

    @Email(message = "{format}")
    @NotEmpty(message = "{required}")
    @Length(max=40, message = "{maxLength}")
    private String email;
}
