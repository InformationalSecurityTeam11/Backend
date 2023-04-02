package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.dto.UserIn;
import team11.backend.InformationSecurityProject.model.Admin;

public interface AdminService {
    Admin register(UserIn userDTO);
}
