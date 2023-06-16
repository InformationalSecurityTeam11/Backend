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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import team11.backend.InformationSecurityProject.dto.*;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.model.*;
import team11.backend.InformationSecurityProject.service.CaptchaService;
import team11.backend.InformationSecurityProject.service.interfaces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final StandardUserService standardUserService;

    private final CertificateRequestService certificateRequestService;
    private final AccountActivationService accountActivationService;
    private final UserService userService;
    private final AuthService authService;
    private  final CaptchaService captchaService;

    @Autowired
    public UserController(StandardUserService standardUserService, CertificateRequestService certificateRequestService, AccountActivationService accountActivationService,
                          UserService userService, AuthService authService, CaptchaService captchaService){

        this.standardUserService = standardUserService;
        this.certificateRequestService = certificateRequestService;
        this.accountActivationService = accountActivationService;
        this.userService = userService;
        this.authService = authService;
        this.captchaService = captchaService;
    }
    @PostMapping(
            value = "/register",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserOut> registerStandardUser(@RequestBody @Valid UserIn userDTO){
        boolean captchaVerified = captchaService.verify(userDTO.getRecaptchaResponse());
        if(!captchaVerified) {
            throw new BadRequestException("Invalid recaptcha token");
        }
        StandardUser user = standardUserService.register(userDTO);
        UserOut userOut = new UserOut(user);
        return new ResponseEntity<>(userOut, HttpStatus.OK);
    }
    @PostMapping(
            value = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> login(@RequestBody @Valid LoginCredentials credentials){
        boolean captchaVerified = captchaService.verify(credentials.getRecaptchaResponse());
        if(!captchaVerified) {
            throw new BadRequestException("Invalid recaptcha token");
        }
        String message = authService.login(credentials);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(
            value = "/login/confirm/{verificationCode}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TokenStateOut> confirmLogin(@NotNull(message = "Field (verificationCode) is required")
                                                          @PathVariable(value = "verificationCode") Integer verificationCode){
        return new ResponseEntity<>(authService.confirmLogin(verificationCode), HttpStatus.OK);
    }

    @PostMapping("/oauth")
    public ResponseEntity<Object> oauthSignIn(@Valid @RequestBody OAuthUserDTO userDTO){
        Boolean isRegistered = this.userService.oauthDoesMailExists(userDTO);
        if (isRegistered){
            User userDetails = this.userService.findUserByEmail(userDTO.getEmail());
            String tokenValue = this.authService.generateToken(userDetails);
            String refreshToken = this.authService.generateRefreshToken(userDetails);
            TokenStateOut token = new TokenStateOut(tokenValue, refreshToken);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }else{
            UserOut newUser = this.userService.regsterOauth(userDTO);
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        }

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
    public ResponseEntity<?> requestPasswordReset(@RequestBody @Valid PasswordResetRequestDTO passwordResetRequestDTO){
        String message = userService.requestPasswordReset(passwordResetRequestDTO);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(
            value = "/password/reset/{resetCode}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> resetPassword(@NotNull(message = "Field (resetCode) is required")
                                           @PathVariable(value = "resetCode") Integer resetCode,
                                           @RequestBody @Valid PasswordResetDTO passwordResetDTO){
        userService.resetPassword(passwordResetDTO, resetCode);
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
