package team11.backend.InformationSecurityProject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "certificate_requests")
public class CertificateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "parent_certificate_id")
    private Certificate parent;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "rejection_reason")
    private String rejection_reason;

    @Column(name = "time_of_acceptance")
    private LocalDateTime acceptanceTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_certificate_id")
    private Certificate linkedCertificate;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @Column(name = "organization")
    private String organization;

    @Column(name = "organization_unit")
    private String organizationUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_state", nullable = false)
    private RequestState requestState;

}
