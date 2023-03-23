package team11.backend.InformationSecurityProject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LoginCredentials {
    @NotBlank(message = "Field (email) is required")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;

    @NotBlank(message = "Field (password) is required")
    @Size(min=6,max = 30,message = "Field (password) cannot be less than 6 characters and more than 30 characters long")
    private String password;

    public LoginCredentials(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }
}
