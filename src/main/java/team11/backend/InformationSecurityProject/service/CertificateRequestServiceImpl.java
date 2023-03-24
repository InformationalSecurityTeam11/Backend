package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.CertificateRequestIn;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.model.*;
import team11.backend.InformationSecurityProject.repository.CertificateRequestRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CertificateRequestServiceImpl implements CertificateRequestService{

    private final CertificateRequestRepository certificateRequestRepository;
    private final CertificateService certificateService;

    @Autowired
    public CertificateRequestServiceImpl(CertificateRequestRepository certificateRequestRepository, CertificateService certificateService){

        this.certificateRequestRepository = certificateRequestRepository;
        this.certificateService = certificateService;
    }

    @Override
    public CertificateRequest createRequest(CertificateRequestIn certificateRequestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        if(user.getUserType().equals("STANDARD")){
            if(certificateRequestDTO.getCertificateType() == CertificateType.ROOT){
                throw new BadRequestException("Standard user cannot request root certificate");
            }
        }else{
            // TODO kreiraj sertifikat
        }
        Certificate parent = certificateService.get(certificateRequestDTO.getParentCertificateId());


        if(Objects.equals(user.getId(), parent.getUser().getId())){
            // TODO kreiraj sertifikat
        }

        CertificateRequest certificateRequest = new CertificateRequest();

        certificateRequest.setParent(parent);
        certificateRequest.setCertificateType(certificateRequestDTO.getCertificateType());
        certificateRequest.setCreationTime(LocalDateTime.now());
        certificateRequest.setOwner(user);

        return certificateRequestRepository.save(certificateRequest);
    }
}
