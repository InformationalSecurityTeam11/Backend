package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.UserIn;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.model.Role;
import team11.backend.InformationSecurityProject.model.StandardUser;
import team11.backend.InformationSecurityProject.repository.StandardUserRepository;

import java.util.Optional;

@Service
public class StandardUserServiceImpl implements StandardUserService{

    private final BCryptPasswordEncoder passwordEncoder;
    private final StandardUserRepository standardUserRepository;
    private final RoleService roleService;

    @Autowired
    public StandardUserServiceImpl(BCryptPasswordEncoder passwordEncoder, StandardUserRepository standardUserRepository, RoleService roleService){
        this.passwordEncoder = passwordEncoder;
        this.standardUserRepository = standardUserRepository;
        this.roleService = roleService;
    }
    @Override
    public StandardUser register(UserIn userDTO) {
        StandardUser user = new StandardUser();

        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setTelephoneNumber(userDTO.getTelephoneNumber());

        Role role = roleService.findRoleByName("ROLE_STANDARD");
        user.setRole(role);

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        try{
            user = standardUserRepository.save(user);
        }catch (DataIntegrityViolationException e){
            throw new BadRequestException("User with given email already exists");
        }
        Optional<StandardUser> test = standardUserRepository.findById(1);
        return user;
    }
}
