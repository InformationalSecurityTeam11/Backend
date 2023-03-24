package team11.backend.InformationSecurityProject.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team11.backend.InformationSecurityProject.dto.CertificateRequestIn;
import team11.backend.InformationSecurityProject.dto.CertificateRequestOut;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.service.CertificateRequestService;
import team11.backend.InformationSecurityProject.service.CertificateService;

@RestController
@RequestMapping(name = "/api/certificate")
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificateRequestService certificateRequestService;

    @Autowired
    public CertificateController(CertificateService certificateService, CertificateRequestService certificateRequestService){

        this.certificateService = certificateService;
        this.certificateRequestService = certificateRequestService;
    }

    @PostMapping(
            name = "/request",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CertificateRequestOut> createCertificateRequest(@RequestBody @Valid CertificateRequestIn certificateRequestDTO){
        CertificateRequest certificateRequest = certificateRequestService.createRequest(certificateRequestDTO);
        return new ResponseEntity<>(new CertificateRequestOut(certificateRequest), HttpStatus.OK);
    }

}
