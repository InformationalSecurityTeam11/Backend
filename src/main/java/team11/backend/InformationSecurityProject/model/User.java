package team11.backend.InformationSecurityProject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.*;

@Entity
@NoArgsConstructor
@Data
@Table(name="users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)

public abstract class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_type", insertable = false, updatable = false, nullable = false)
    private String userType;        // ADMIN, STANDARD
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "surname", nullable = false)
    private String surname;
    @Column(name = "telephone_number", nullable = false)
    private String telephoneNumber;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "old_password")
    private List<String> oldPasswords;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;
    @Column(name = "last_password_reset_date")
    private Timestamp lastPasswordResetDate;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    public void setPassword(String password){
        Timestamp now = new Timestamp((new Date()).getTime());
        this.setLastPasswordResetDate(now);
        this.password = password;
    }




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.role);
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -30);
        Date dateBefore30Days = cal.getTime();

        return !this.lastPasswordResetDate.before(dateBefore30Days);
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

}
