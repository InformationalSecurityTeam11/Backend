package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.dto.UserIn;
import team11.backend.InformationSecurityProject.model.StandardUser;

public interface StandardUserService {
    StandardUser register(UserIn userDTO);
}
