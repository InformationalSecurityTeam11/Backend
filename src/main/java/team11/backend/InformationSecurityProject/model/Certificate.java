package team11.backend.InformationSecurityProject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "Certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CertificateType type;
}
