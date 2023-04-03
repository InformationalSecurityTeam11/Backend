package team11.backend.InformationSecurityProject.controller;

import jakarta.validation.Valid;
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
import team11.backend.InformationSecurityProject.model.Admin;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.model.StandardUser;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.security.RefreshTokenService;
import team11.backend.InformationSecurityProject.security.TokenUtils;
import team11.backend.InformationSecurityProject.service.interfaces.AdminService;
import team11.backend.InformationSecurityProject.service.interfaces.CertificateRequestService;
import team11.backend.InformationSecurityProject.service.interfaces.StandardUserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final StandardUserService standardUserService;

    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final RefreshTokenService refreshTokenService;
    private final AdminService adminService;
    private final CertificateRequestService certificateRequestService;

    @Autowired
    public UserController(StandardUserService standardUserService, AuthenticationManager authenticationManager, TokenUtils tokenUtils,
                          RefreshTokenService refreshTokenService, AdminService adminService, CertificateRequestService certificateRequestService){

        this.standardUserService = standardUserService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.refreshTokenService = refreshTokenService;
        this.adminService = adminService;
        this.certificateRequestService = certificateRequestService;
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
            value = "/register/admin",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserOut> registerAdmin(@RequestBody @Valid UserIn userDTO){
        Admin user = adminService.register(userDTO);
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
    public ResponseEntity<List<RequestDTO>> getAllRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<RequestDTO> requestDTOS = new ArrayList<>();
        for (CertificateRequest request : this.certificateRequestService.getCertificateRequestByOwner(user)) {
            requestDTOS.add(new RequestDTO(request));
        }
        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }
}
