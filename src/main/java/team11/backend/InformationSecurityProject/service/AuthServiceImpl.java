package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import team11.backend.InformationSecurityProject.model.AccountActivationMethod;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.model.VerificationCode;
import team11.backend.InformationSecurityProject.model.VerificationCodeType;
import team11.backend.InformationSecurityProject.repository.VerificationCodeRepository;
import team11.backend.InformationSecurityProject.security.RefreshTokenService;
import team11.backend.InformationSecurityProject.security.TokenUtils;
import team11.backend.InformationSecurityProject.service.interfaces.AuthService;
import team11.backend.InformationSecurityProject.service.interfaces.MailService;

import java.time.Duration;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final RefreshTokenService refreshTokenService;
    private final VerificationCodeRepository verificationCodeRepository;

    private final MailService mailService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, TokenUtils tokenUtils, RefreshTokenService refreshTokenService, VerificationCodeRepository verificationCodeRepository, MailService mailService){

        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.refreshTokenService = refreshTokenService;
        this.verificationCodeRepository = verificationCodeRepository;
        this.mailService = mailService;
    }

    @Override
    public TokenStateOut confirmLogin(int verificationCode){
        Optional<VerificationCode> verificationCodeOptional = verificationCodeRepository.findByCodeAndType(verificationCode, VerificationCodeType.LOGIN);
        if(verificationCodeOptional.isEmpty()){
            throw new BadRequestException("Reset code is not valid");
        }

        VerificationCode verification = verificationCodeOptional.get();
        User user = verification.getUser();
        if(!verification.isValid()){
            throw new BadRequestException("Reset code is not valid");
        }

        String jwt = this.tokenUtils.generateToken(user);
        String refreshToken = this.refreshTokenService.createRefreshToken(user).getToken();
        return new TokenStateOut(jwt, refreshToken);
    }

    @Override
    public String login(LoginCredentials credentials) {
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

        int durationInMinutes = 15;
        VerificationCode verificationCode = new VerificationCode(user, Duration.ofMinutes(durationInMinutes), VerificationCodeType.LOGIN);
        verificationCodeRepository.save(verificationCode);
        mailService.sendVerificationCode(user.getEmail(), verificationCode.getCode(), AccountActivationMethod.EMAIL, VerificationCodeType.LOGIN, "http://localhost:4200/confirmLogin");

        return "Check your email for verification code";
    }
}
