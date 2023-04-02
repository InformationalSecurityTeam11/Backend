package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.UserIn;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.model.Admin;
import team11.backend.InformationSecurityProject.model.Role;
import team11.backend.InformationSecurityProject.repository.AdminRepository;
import team11.backend.InformationSecurityProject.service.interfaces.AdminService;
import team11.backend.InformationSecurityProject.service.interfaces.RoleService;

@Service
public class AdminServiceImpl implements AdminService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final RoleService roleService;

    @Autowired
    public AdminServiceImpl(BCryptPasswordEncoder passwordEncoder, AdminRepository adminRepository, RoleService roleService){
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.roleService = roleService;
    }
    @Override
    public Admin register(UserIn userDTO) {
        Admin admin = new Admin();

        admin.setEmail(userDTO.getEmail());
        admin.setName(userDTO.getName());
        admin.setSurname(userDTO.getSurname());
        admin.setTelephoneNumber(userDTO.getTelephoneNumber());

        Role role = roleService.findRoleByName("ROLE_ADMIN");
        admin.setRole(role);

        admin.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        try{
            admin = adminRepository.save(admin);
        }catch (DataIntegrityViolationException e){
            throw new BadRequestException("User with given email already exists");
        }

        return admin;
    }
}
