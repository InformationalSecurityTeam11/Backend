package team11.backend.InformationSecurityProject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TokenStateOut {
    private String accessToken;
    private String refreshToken;

    public TokenStateOut(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
