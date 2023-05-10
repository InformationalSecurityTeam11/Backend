package team11.backend.InformationSecurityProject.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import team11.backend.InformationSecurityProject.dto.*;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.model.*;
import team11.backend.InformationSecurityProject.security.RefreshTokenService;
import team11.backend.InformationSecurityProject.security.TokenUtils;
import team11.backend.InformationSecurityProject.service.interfaces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final StandardUserService standardUserService;
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final RefreshTokenService refreshTokenService;
    private final CertificateRequestService certificateRequestService;
    private final AccountActivationService accountActivationService;
    private final UserService userService;

    @Autowired
    public UserController(StandardUserService standardUserService, AuthenticationManager authenticationManager, TokenUtils tokenUtils,
                          RefreshTokenService refreshTokenService, CertificateRequestService certificateRequestService, AccountActivationService accountActivationService, UserService userService){

        this.standardUserService = standardUserService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.refreshTokenService = refreshTokenService;
        this.certificateRequestService = certificateRequestService;
        this.accountActivationService = accountActivationService;
        this.userService = userService;
    }
    @PostMapping(
            value = "/register",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserOut> registerStandardUser(@RequestBody @Valid UserIn userDTO){
        StandardUser user = standardUserService.register(userDTO);
        UserOut userOut = new UserOut(user);
        return new ResponseEntity<>(userOut, HttpStatus.OK);
    }
    @PostMapping(
            value = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TokenStateOut> login(@RequestBody @Valid LoginCredentials credentials){
        Authentication authentication;
        try {
             authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));

        }catch (AuthenticationException e){
            throw new BadRequestException("Wrong email or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String jwt = this.tokenUtils.generateToken(user);
        String refreshToken = this.refreshTokenService.createRefreshToken(user).getToken();
        return new ResponseEntity<>(new TokenStateOut(jwt, refreshToken), HttpStatus.OK);
    }

    @GetMapping(
            value = "/logout",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<?> logout(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!(auth instanceof AnonymousAuthenticationToken)){
            SecurityContextHolder.clearContext();
            return new ResponseEntity<>("Logout successful", HttpStatus.OK);
        }
        throw new BadRequestException("User is not authenticated");
    }

    @GetMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<List<CertificateRequestOut>> getAllRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<CertificateRequestOut> requestDTOS = new ArrayList<>();
        List<CertificateRequest> requests = user.getUserType().equals("ADMIN") ? certificateRequestService.getAll() : certificateRequestService.getCertificateRequestByOwner(user);
        for (CertificateRequest request : requests) {
            requestDTOS.add(new CertificateRequestOut(request));
        }
        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }

    @PostMapping(
            value = "/password/reset/request",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<?> requestPasswordReset(@RequestBody @Valid PasswordResetRequestDTO passwordResetRequestDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String message = userService.requestPasswordReset(user, passwordResetRequestDTO.getPasswordResetMethod());
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(
            value = "/password/reset/{resetCode}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<?> resetPassword(@NotNull(message = "Field (resetCode) is required")
                                           @PathVariable(value = "resetCode") Integer resetCode,
                                           @RequestBody @Valid PasswordResetDTO passwordResetDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        userService.resetPassword(passwordResetDTO, resetCode, user);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Successful password reset");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping(
            value = "/activate/{activationCode}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> activateAccount(@NotNull(message = "Field (activationCode) is required")
                                               @Positive(message = "Activation code must be positive")
                                               @PathVariable(value="activationCode") Integer activationCode){

        accountActivationService.activateAccount(activationCode);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Successful account activation");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
