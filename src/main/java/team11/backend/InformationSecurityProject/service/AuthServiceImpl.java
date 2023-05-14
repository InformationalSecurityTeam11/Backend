package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.LoginCredentials;
import team11.backend.InformationSecurityProject.dto.TokenStateOut;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.exceptions.ForbiddenException;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.security.RefreshTokenService;
import team11.backend.InformationSecurityProject.security.TokenUtils;
import team11.backend.InformationSecurityProject.service.interfaces.AuthService;

import java.sql.Timestamp;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, TokenUtils tokenUtils, RefreshTokenService refreshTokenService){

        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public TokenStateOut login(LoginCredentials credentials) {
        Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));

        }
        catch (CredentialsExpiredException e){
            throw new ForbiddenException("Credentials expired, reset your password to continue");
        }
        catch (AuthenticationException e){
            throw new BadRequestException("Wrong email or password");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        String jwt = this.tokenUtils.generateToken(user);
        String refreshToken = this.refreshTokenService.createRefreshToken(user).getToken();
        return new TokenStateOut(jwt, refreshToken);
    }
}
