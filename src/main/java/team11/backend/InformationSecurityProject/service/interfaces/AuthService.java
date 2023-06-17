package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.dto.LoginCredentials;
import team11.backend.InformationSecurityProject.dto.TokenStateOut;
import team11.backend.InformationSecurityProject.model.User;

public interface AuthService {
    TokenStateOut confirmLogin(int verificationCode);

    String login(LoginCredentials credentials);

    String generateToken(User user);
    String generateRefreshToken(User user);
}
