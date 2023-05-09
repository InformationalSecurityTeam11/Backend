package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.UserIn;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.model.AccountActivation;
import team11.backend.InformationSecurityProject.model.Role;
import team11.backend.InformationSecurityProject.model.StandardUser;
import team11.backend.InformationSecurityProject.repository.StandardUserRepository;
import team11.backend.InformationSecurityProject.service.interfaces.AccountActivationService;
import team11.backend.InformationSecurityProject.service.interfaces.MailService;
import team11.backend.InformationSecurityProject.service.interfaces.RoleService;
import team11.backend.InformationSecurityProject.service.interfaces.StandardUserService;

import java.time.Duration;

@Service
public class StandardUserServiceImpl implements StandardUserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final StandardUserRepository standardUserRepository;
    private final RoleService roleService;
    private final MailService mailService;
    private final AccountActivationService accountActivationService;

    @Autowired
    public StandardUserServiceImpl(BCryptPasswordEncoder passwordEncoder, StandardUserRepository standardUserRepository, RoleService roleService, MailServiceImpl mailService, AccountActivationService accountActivationService){
        this.passwordEncoder = passwordEncoder;
        this.standardUserRepository = standardUserRepository;
        this.roleService = roleService;
        this.mailService = mailService;
        this.accountActivationService = accountActivationService;
    }
    @Override
    public StandardUser register(UserIn userDTO) {
        StandardUser user = new StandardUser();

        user.setEnabled(false);
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setTelephoneNumber(userDTO.getTelephoneNumber());

        Role role = roleService.findRoleByName("ROLE_STANDARD");
        user.setRole(role);

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        StandardUser userSaved;
        try{
            userSaved = standardUserRepository.save(user);
        }catch (DataIntegrityViolationException e){
            throw new BadRequestException("User with given email already exists");
        }

        AccountActivation activation = new AccountActivation(user, Duration.ofDays(1));
        accountActivationService.insert(activation);

        mailService.sendActivation(userSaved, activation.getCode(), userDTO.getActivationMethod());
        return userSaved;
    }
}
