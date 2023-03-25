package team11.backend.InformationSecurityProject.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.exceptions.ForbiddenException;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.RefreshToken;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.service.interfaces.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    // Period vazenja refresh tokena - 1 dan
    @Value("86400000")
    private int REFRESH_TOKEN_EXPIRES_IN;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserService userService, RefreshTokenRepository refreshTokenRepository){
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken findByToken(String token) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(token);
        if (refreshTokenOptional.isPresent()){
            return refreshTokenOptional.get();
        }
        throw new NotFoundException("Refresh token not found!");
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plus(REFRESH_TOKEN_EXPIRES_IN, ChronoUnit.MILLIS));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new ForbiddenException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public int deleteByUserId(Integer userId) {
        return refreshTokenRepository.deleteByUser(userService.getUser(userId));
    }
}
