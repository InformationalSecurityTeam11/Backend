package team11.backend.InformationSecurityProject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.random.RandomGenerator;

@Entity
@Data
@NoArgsConstructor
@Table(name = "password_resets")
public class PasswordReset {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;
    @Column(name = "life_span", nullable = false)
    private Duration lifeSpan;
    @Id
    @Column(name = "code", nullable = false)
    private Integer code;

    public boolean isApproved(Integer userID){
        return getDateCreated().plus(getLifeSpan()).isAfter(LocalDateTime.now()) && Objects.equals(getUser().getId(), userID);
    }
    public PasswordReset(User user, Duration lifeSpan) {
        this.user = user;
        this.dateCreated = LocalDateTime.now();
        this.lifeSpan = lifeSpan;
        this.code = Math.abs(user.hashCode() + dateCreated.hashCode() + RandomGenerator.getDefault().nextInt(100));
    }
}
