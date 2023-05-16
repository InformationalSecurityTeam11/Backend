package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.dto.LoginCredentials;
import team11.backend.InformationSecurityProject.dto.TokenStateOut;

public interface AuthService {
    TokenStateOut confirmLogin(int verificationCode);

    String login(LoginCredentials credentials);
}
