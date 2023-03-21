package team11.backend.InformationSecurityProject.security;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import team11.backend.InformationSecurityProject.model.RefreshToken;
import team11.backend.InformationSecurityProject.model.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    @Transactional
    @Modifying
    int deleteByUser(User user);
}
