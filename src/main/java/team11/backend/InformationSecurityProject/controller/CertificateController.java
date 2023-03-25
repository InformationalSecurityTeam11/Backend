package team11.backend.InformationSecurityProject.controller;

import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team11.backend.InformationSecurityProject.dto.CertificateInfoDTO;
import team11.backend.InformationSecurityProject.dto.SubjectInfoDTO;
import team11.backend.InformationSecurityProject.service.CertificateService;
import team11.backend.InformationSecurityProject.service.interfaces.ICertificateService;
import team11.backend.InformationSecurityProject.utils.CertificateUtility;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final ICertificateService certificateService;

    @Autowired
    public CertificateController(ICertificateService certificateService) {
        this.certificateService = certificateService;
    }


    @PreAuthorize("permitAll()")
    @PostMapping("/self-signed")
    public ResponseEntity<X509Certificate> generateSelfSignedCertificate(SubjectInfoDTO subjectInfo) {
        try {
            KeyPair keyPair = CertificateUtility.generateKeyPair();
            X500Name subject = CertificateUtility.generateX500Name(subjectInfo);
            X509Certificate certificate = certificateService.createSelfSignedCertificate(keyPair, subject, 365);
            return ResponseEntity.ok().body(certificate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity testic() {
        return ResponseEntity.status(HttpStatus.OK).body("mjau");
    }


    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<CertificateInfoDTO> getAllCertificates() {
        return null;
    }



}
