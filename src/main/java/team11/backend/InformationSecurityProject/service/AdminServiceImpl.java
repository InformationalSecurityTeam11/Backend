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

}
