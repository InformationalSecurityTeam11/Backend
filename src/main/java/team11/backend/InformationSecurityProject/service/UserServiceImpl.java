package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.OAuthUserDTO;
import team11.backend.InformationSecurityProject.dto.PasswordResetDTO;
import team11.backend.InformationSecurityProject.dto.PasswordResetRequestDTO;
import team11.backend.InformationSecurityProject.dto.UserOut;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.*;
import team11.backend.InformationSecurityProject.repository.VerificationCodeRepository;
import team11.backend.InformationSecurityProject.repository.UserRepository;
import team11.backend.InformationSecurityProject.service.interfaces.MailService;
import team11.backend.InformationSecurityProject.service.interfaces.RoleService;
import team11.backend.InformationSecurityProject.service.interfaces.UserService;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RoleService roleService;
    private final  CaptchaService captchaService;
    private final VerificationCodeRepository verificationCodeRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, MailService mailService, VerificationCodeRepository verificationCodeRepository, AuthenticationManager authenticationManager1, CaptchaService captchaService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.roleService = roleService;
        this.verificationCodeRepository = verificationCodeRepository;
        this.captchaService = captchaService;
    }

    @Override
    public User getUser(Integer id) {
        Optional<User> userOpt = userRepository.findById(id);
        if(userOpt.isPresent()){
            return userOpt.get();
        }else {
            throw new NotFoundException("User with given id does not exist");
        }
    }

    @Override
    public String requestPasswordReset(PasswordResetRequestDTO passwordResetRequestDTO){
        int durationInMinutes = 15;

        Optional<User> userOpt;
        if(passwordResetRequestDTO.getPasswordResetMethod() == AccountActivationMethod.EMAIL){
            userOpt = userRepository.findUserByEmail(passwordResetRequestDTO.getContact());
            if(userOpt.isEmpty()){
                throw new BadRequestException("Invalid email address");
            }
        }else {
            userOpt = userRepository.findUserByTelephoneNumber(passwordResetRequestDTO.getContact());
            if(userOpt.isEmpty()){
                throw new BadRequestException("Invalid phone number");
            }
        }
        User user = userOpt.get();

        VerificationCode verificationCode = new VerificationCode(user, Duration.ofMinutes(durationInMinutes), VerificationCodeType.PASSWORD_RESET);
        verificationCodeRepository.save(verificationCode);
        mailService.sendVerificationCode(passwordResetRequestDTO.getContact(), verificationCode.getCode(), passwordResetRequestDTO.getPasswordResetMethod(), VerificationCodeType.PASSWORD_RESET, "http://localhost:4200/passwordReset");

        if(passwordResetRequestDTO.getPasswordResetMethod() == AccountActivationMethod.EMAIL){
            return "Check your email for reset code";
        }
        return "Message has been sent to your mobile number";
    }

    @Override
    public void resetPassword(PasswordResetDTO passwordResetDTO, Integer resetCode){
        Optional<VerificationCode> verificationCodeOptional = verificationCodeRepository.findByCodeAndType(resetCode, VerificationCodeType.PASSWORD_RESET);
        if(verificationCodeOptional.isEmpty()){
            throw new BadRequestException("Reset code is not valid");
        }

        VerificationCode verificationCode = verificationCodeOptional.get();
        User user = verificationCode.getUser();
        if(!verificationCode.isValid()){
            throw new BadRequestException("Reset code is not valid");
        }

        if(!passwordResetDTO.getNewPassword().equals(passwordResetDTO.getNewPasswordConfirmation())){
            throw new BadRequestException("Passwords are not matching");
        }
        if(passwordEncoder.matches(passwordResetDTO.getNewPassword(), user.getPassword())){
            throw new BadRequestException("New password cannot be previous password");
        }
        if(user.getOldPasswords().stream().anyMatch(oldPassword -> passwordEncoder.matches(passwordResetDTO.getNewPassword(), oldPassword))){
            throw new BadRequestException("New password cannot be previous password");
        }

        String newPasswordEncoded = passwordEncoder.encode(passwordResetDTO.getNewPassword());
        user.setPassword(newPasswordEncoded);

        userRepository.save(user);
        verificationCodeRepository.delete(verificationCode);
    }

    private boolean emailExists(String email){
        Optional<User> user = this.userRepository.findUserByEmail(email);
        return user.isPresent();
    }

    private User getUserFromOauthUserDTO(OAuthUserDTO oauthUserDTO){
        StandardUser user = new StandardUser();
        user.setEmail(oauthUserDTO.getEmail());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user.setPassword(passwordEncoder.encode("123456"));
        Role role = roleService.findRoleByName("ROLE_STANDARD");
        user.setRole(role);
        user.setTelephoneNumber("+0238623716");
        user.setSurname(oauthUserDTO.getSurname());
        user.setName(oauthUserDTO.getName());
        user.setLastPasswordResetDate(new Timestamp(new Date().getTime()));
        user.setEnabled(true);
        user = this.userRepository.save(user);
        return user;
    }

    @Override
    public Boolean oauthDoesMailExists(OAuthUserDTO userDTO) {
        return this.emailExists(userDTO.getEmail());
    }

    @Override
    public UserOut regsterOauth(OAuthUserDTO userDTO) {
        User user = getUserFromOauthUserDTO(userDTO);
        return new UserOut(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email).orElse(null);
    }


}
