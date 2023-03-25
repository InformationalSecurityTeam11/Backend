package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.repository.UserRepository;
import team11.backend.InformationSecurityProject.service.interfaces.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
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
}
