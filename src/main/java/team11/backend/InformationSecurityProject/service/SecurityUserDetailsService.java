package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.repository.UserRepository;

import java.util.Optional;

@Service
public class SecurityUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public SecurityUserDetailsService() {
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findUserByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        return user.get();
    }
}
