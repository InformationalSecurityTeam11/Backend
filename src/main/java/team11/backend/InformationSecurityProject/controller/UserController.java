package team11.backend.InformationSecurityProject.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team11.backend.InformationSecurityProject.dto.LoginCredentials;
import team11.backend.InformationSecurityProject.dto.TokenStateOut;
import team11.backend.InformationSecurityProject.dto.UserIn;
import team11.backend.InformationSecurityProject.dto.UserOut;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.model.StandardUser;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.security.RefreshTokenService;
import team11.backend.InformationSecurityProject.security.TokenUtils;
import team11.backend.InformationSecurityProject.service.StandardUserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final StandardUserService standardUserService;

    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public UserController(StandardUserService standardUserService, AuthenticationManager authenticationManager, TokenUtils tokenUtils, RefreshTokenService refreshTokenService){

        this.standardUserService = standardUserService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.refreshTokenService = refreshTokenService;
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

}
