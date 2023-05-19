package team11.backend.InformationSecurityProject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import team11.backend.InformationSecurityProject.model.AccountActivationMethod;

@Data
@NoArgsConstructor
public class PasswordResetRequestDTO {
    @NotNull(message = "Field (passwordResetMethod) is required")
    private AccountActivationMethod passwordResetMethod;
    @NotNull(message = "Field (contact) is required")   // This field contains email or phone number depending on method
    private String contact;
}
