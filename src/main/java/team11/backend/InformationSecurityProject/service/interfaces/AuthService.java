package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.dto.LoginCredentials;
import team11.backend.InformationSecurityProject.dto.TokenStateOut;

public interface AuthService {
    TokenStateOut login(LoginCredentials credentials);
}
