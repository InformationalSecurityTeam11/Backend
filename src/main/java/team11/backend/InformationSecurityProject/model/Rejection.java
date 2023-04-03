package team11.backend.InformationSecurityProject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "Rejections")
public class Rejection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "parent_certificate_id")
    private Certificate parent;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "time_of_rejection")
    private LocalDateTime rejectionTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @Column(name = "organization")
    private String organization;

    @Column(name = "organization_unit")
    private String organizationUnit;

    @Column(name = "rejection_reason")
    private String rejection_reason;

}
