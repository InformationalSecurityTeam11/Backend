package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.PasswordResetDTO;
import team11.backend.InformationSecurityProject.dto.PasswordResetRequestDTO;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.AccountActivationMethod;
import team11.backend.InformationSecurityProject.model.PasswordReset;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.repository.PasswordResetRepository;
import team11.backend.InformationSecurityProject.repository.UserRepository;
import team11.backend.InformationSecurityProject.service.interfaces.MailService;
import team11.backend.InformationSecurityProject.service.interfaces.UserService;

import java.time.Duration;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PasswordResetRepository passwordResetRepository;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, MailService mailService, PasswordResetRepository passwordResetRepository, AuthenticationManager authenticationManager1){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.passwordResetRepository = passwordResetRepository;
        this.authenticationManager = authenticationManager1;
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

        PasswordReset passwordReset = new PasswordReset(user, Duration.ofMinutes(durationInMinutes));
        passwordResetRepository.save(passwordReset);
        mailService.sendPasswordReset(passwordResetRequestDTO.getContact(), passwordReset.getCode(), passwordResetRequestDTO.getPasswordResetMethod());

        if(passwordResetRequestDTO.getPasswordResetMethod() == AccountActivationMethod.EMAIL){
            return "Check your email for reset code";
        }
        return "Message has been sent to your mobile number";
    }

    @Override
    public void resetPassword(PasswordResetDTO passwordResetDTO, Integer resetCode){
        Optional<PasswordReset> passwordResetInstanceOptional = passwordResetRepository.findById(resetCode);
        if(passwordResetInstanceOptional.isEmpty()){
            throw new BadRequestException("Reset code is not valid");
        }

        PasswordReset passwordReset = passwordResetInstanceOptional.get();
        User user = passwordReset.getUser();
        if(!passwordReset.isValid()){
            throw new BadRequestException("Reset code is not valid");
        }

        if(!passwordResetDTO.getNewPassword().equals(passwordResetDTO.getNewPasswordConfirmation())){
            throw new BadRequestException("Passwords are not matching");
        }

        String newPasswordEncoded = passwordEncoder.encode(passwordResetDTO.getNewPassword());
        String oldPassword = user.getPassword();
        user.setPassword(newPasswordEncoded);
        user.getOldPasswords().add(oldPassword);

        userRepository.save(user);
        passwordResetRepository.delete(passwordReset);
    }
}
