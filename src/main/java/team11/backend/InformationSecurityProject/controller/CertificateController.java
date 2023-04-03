package team11.backend.InformationSecurityProject.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team11.backend.InformationSecurityProject.dto.*;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.service.interfaces.CertificatePreviewService;
import team11.backend.InformationSecurityProject.service.interfaces.CertificateRequestService;
import team11.backend.InformationSecurityProject.service.interfaces.ICertificateService;
import team11.backend.InformationSecurityProject.utils.CertificateUtility;

import java.util.List;
import java.util.Set;
@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final ICertificateService certificateService;
    private final CertificateRequestService certificateRequestService;
    private final CertificateUtility certificateUtility;
    private final CertificatePreviewService certificatePreviewService;

    @Autowired
    public CertificateController(ICertificateService certificateService, CertificateRequestService certificateRequestService, CertificateUtility certificateUtility, CertificatePreviewService certificatePreviewService) {
        this.certificateService = certificateService;
        this.certificateRequestService = certificateRequestService;
        this.certificateUtility = certificateUtility;
        this.certificatePreviewService = certificatePreviewService;
    }

    @PostMapping(
        value = "/request",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
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

    @PostMapping(
            value = "/validate",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity<ValidCertificateDTO> validateCertificate(@RequestBody @Valid ValidateCertificateDTO validateCertificateDTO){
        Certificate certificate = null;
        try{
            certificate = this.certificatePreviewService.validateCertificate(validateCertificateDTO.getSerialNumber());
            if(certificate == null){
                return new ResponseEntity<>(new ValidCertificateDTO(false), HttpStatus.OK);
            }
        }catch (NotFoundException e){
            throw new BadRequestException("Invalid serial number");
        }
        return new ResponseEntity<>(new ValidCertificateDTO(certificate), HttpStatus.OK);
    }

    @PostMapping(
            value = "/approve",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity approveCertificate(@RequestBody @Valid ValidateCertificateDTO validation){
        int id = validation.getSerialNumber().intValueExact();
        try{
            Boolean response = certificateRequestService.approve(id);
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("There is no specified request");
        }
        catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity(HttpStatus.OK);

    }

    @PostMapping(
            value = "/reject",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAnyRole('STANDARD', 'ADMIN')")
    public ResponseEntity rejectCertificate(@RequestBody @Valid RejectionDTO rejection){
        try{
            Boolean response = certificateRequestService.reject(rejection.getId(), rejection.getReason());
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("There is no specified request");
        }
        catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity(HttpStatus.OK);

    }

}
