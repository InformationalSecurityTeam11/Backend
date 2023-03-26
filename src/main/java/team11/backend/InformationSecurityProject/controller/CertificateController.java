package team11.backend.InformationSecurityProject.controller;

import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team11.backend.InformationSecurityProject.dto.CertificateInfoDTO;
import team11.backend.InformationSecurityProject.dto.SubjectInfoDTO;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.service.CertificateService;
import team11.backend.InformationSecurityProject.service.interfaces.ICertificateService;
import team11.backend.InformationSecurityProject.utils.CertificateUtility;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final ICertificateService certificateService;
    private final CertificateRequestService certificateRequestService;

    @Autowired
    public CertificateController(ICertificateService certificateService, CertificateRequestService certificateRequestService) {
        this.certificateService = certificateService;
        this.certificateRequestService = certificateRequestService;
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

    @PostMapping(
        name = "/request",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CertificateRequestOut> createCertificateRequest(@RequestBody @Valid CertificateRequestIn certificateRequestDTO){
        CertificateRequest certificateRequest = certificateRequestService.createRequest(certificateRequestDTO);
        return new ResponseEntity<>(new CertificateRequestOut(certificateRequest), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Set<CertificateInfoDTO>> getAllCertificates() {
        List<Certificate> certificates = this.certificateService.getAll();
        return new ResponseEntity<>(this.certificateService.getCertificatesDTOS(certificates), HttpStatus.OK);
    }



}
