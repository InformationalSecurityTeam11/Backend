package team11.backend.InformationSecurityProject.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team11.backend.InformationSecurityProject.dto.*;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.service.CertificatePreviewServiceImpl;
import team11.backend.InformationSecurityProject.service.KeyStoreService;
import team11.backend.InformationSecurityProject.service.interfaces.CertificatePreviewService;
import team11.backend.InformationSecurityProject.service.interfaces.CertificateRequestService;
import team11.backend.InformationSecurityProject.service.interfaces.ICertificateService;
import team11.backend.InformationSecurityProject.utils.CertificateUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.Base64;
import java.util.List;
import java.util.Set;
@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final ICertificateService certificateService;
    private final CertificateRequestService certificateRequestService;
    private final CertificatePreviewService certificatePreviewService;
    private final KeyStoreService keyStoreService;

    @Autowired
    public CertificateController(ICertificateService certificateService, CertificateRequestService certificateRequestService, CertificatePreviewService certificatePreviewService,
                                 KeyStoreService keyStoreService) {
        this.certificateService = certificateService;
        this.certificateRequestService = certificateRequestService;
        this.certificatePreviewService = certificatePreviewService;
        this.keyStoreService = keyStoreService;
    }

    @PostMapping(
        value = "/request",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<CertificateRequestOut> createCertificateRequest(@RequestBody @Valid CertificateRequestIn certificateRequestDTO){
        CertificateRequest certificateRequest = certificateRequestService.createRequest(certificateRequestDTO);
        return new ResponseEntity<>(new CertificateRequestOut(certificateRequest), HttpStatus.OK);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("permitAll()")
    public ResponseEntity<Set<CertificateInfoDTO>> getAllCertificates() {
        List<Certificate> certificates = this.certificateService.getAll();
        return new ResponseEntity<>(this.certificateService.getCertificatesDTOS(certificates), HttpStatus.OK);
    }

    @PostMapping(
            value = "/validate",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<ValidCertificateDTO> validateCertificate(@RequestBody @Valid ValidateCertificateDTO validateCertificateDTO){
        CertificatePreviewService.CertificateValidationObject validationObject = this.certificatePreviewService.validateCertificate(validateCertificateDTO.getSerialNumber());
        return new ResponseEntity<>(new ValidCertificateDTO(validationObject.certificate, validationObject.isValid), HttpStatus.OK);
    }

    @PostMapping(
            value = "/approve",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<?> approveCertificate(@RequestBody @Valid ApproveDTO approval){
        certificateRequestService.approve(approval.getId());
        return new ResponseEntity<>("Certificate request approved", HttpStatus.OK);
    }

    @PostMapping(
            value = "/reject",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<?> rejectCertificate(@RequestBody @Valid RejectionDTO rejection){
        certificateRequestService.reject(rejection.getId(), rejection.getReason());
        return new ResponseEntity<>("Certificate request rejected", HttpStatus.OK);
    }

    @GetMapping(value = "/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<RequestInfoDTO>> getAllRequests() {
        List<CertificateRequest> requests = this.certificateRequestService.getAll();
        return new ResponseEntity<>(this.certificateRequestService.getRequestsDTOS(requests), HttpStatus.OK);

    }


    @GetMapping(value = "/download/{id}")
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<?> downloadCertificate(@PathVariable Integer id) throws CertificateEncodingException, IOException {
        Certificate certificateInfo =  this.certificateService.getById(id);
        if (certificateInfo == null) {
            return new ResponseEntity<>("Certificate not found", HttpStatus.OK);

        }
        X509Certificate certificate = (X509Certificate) this.keyStoreService.getCertificate(certificateInfo.getSerialNumber());
        byte[] certificateData = certificate.getEncoded();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write("-----BEGIN CERTIFICATE-----\n".getBytes());
        outputStream.write(Base64.getEncoder().encode(certificateData));
        outputStream.write("\n-----END CERTIFICATE-----\n".getBytes());
        byte[] fileData = outputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "certificate.cer");
        headers.setContentLength(fileData.length);
        return new ResponseEntity<byte[]>(fileData, headers, HttpStatus.OK);
    }

    @PostMapping("/verify/upload")
    public ResponseEntity<String> verifyUploadedCertificate(@RequestPart("file") MultipartFile file) {
        try {
            boolean isValid = certificatePreviewService.verifyUploadedCertificate(file.getInputStream());
            if (isValid) {
                return ResponseEntity.ok("Certificate is valid");
            } else {
                return ResponseEntity.ok("Certificate is invalid");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying certificate");
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokeCertificate(@RequestParam("serialNumber") BigInteger serialNumber) {
        String alias = "CERTIFICATE_" + serialNumber;
        
    }

    @GetMapping("/revoke/check/")
    public ResponseEntity<Boolean> isRevoked(@RequestBody X509Certificate certificate) {
        boolean revoked = certificatePreviewService.isRevoked(certificate);
        return ResponseEntity.ok(revoked);
    }

}
