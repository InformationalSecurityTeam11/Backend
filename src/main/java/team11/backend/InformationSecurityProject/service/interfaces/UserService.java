package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.dto.OAuthUserDTO;
import team11.backend.InformationSecurityProject.dto.PasswordResetDTO;
import team11.backend.InformationSecurityProject.dto.PasswordResetRequestDTO;
import team11.backend.InformationSecurityProject.dto.UserOut;
import team11.backend.InformationSecurityProject.model.User;

public interface UserService {
    User getUser(Integer id);

    String requestPasswordReset(PasswordResetRequestDTO passwordResetRequestDTO);

    void resetPassword(PasswordResetDTO passwordResetDTO, Integer resetCode);

    Boolean oauthDoesMailExists(OAuthUserDTO userDTO);

    UserOut regsterOauth(OAuthUserDTO userDTO);

    User findUserByEmail(String email);
}
