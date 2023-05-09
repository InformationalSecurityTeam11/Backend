package team11.backend.InformationSecurityProject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Data
@Getter
@Setter
public class PasswordResetDTO {
    @NotBlank(message = "Field (oldPassword) is required")
    private String oldPassword;

    @NotBlank(message = "Field (newPassword) is required")
    @Size(min=6, max = 30, message = "Field (newPassword) cannot be less than 6 characters and more than 30 characters long")
    private String newPassword;

    @NotBlank(message = "Field (newPasswordConfirmation) is required")
    private String newPasswordConfirmation;

    public PasswordResetDTO(String oldPassword, String newPassword, String newPasswordConfirmation) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
