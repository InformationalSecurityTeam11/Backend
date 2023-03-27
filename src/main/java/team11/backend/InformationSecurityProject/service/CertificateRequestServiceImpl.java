package team11.backend.InformationSecurityProject.service;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.CertificatePair;
import team11.backend.InformationSecurityProject.dto.CertificateRequestIn;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.model.*;
import team11.backend.InformationSecurityProject.repository.CertificateRequestRepository;
import team11.backend.InformationSecurityProject.service.interfaces.CertificatePreviewService;
import team11.backend.InformationSecurityProject.service.interfaces.CertificateRequestService;
import team11.backend.InformationSecurityProject.service.interfaces.ICertificateService;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CertificateRequestServiceImpl implements CertificateRequestService {

    private final CertificateRequestRepository certificateRequestRepository;
    private final CertificatePreviewService certificatePreviewService;
    private final ICertificateService certificateService;

    @Autowired
    public CertificateRequestServiceImpl(CertificateRequestRepository certificateRequestRepository, CertificatePreviewService certificatePreviewService, ICertificateService certificateService){

        this.certificateRequestRepository = certificateRequestRepository;
        this.certificatePreviewService = certificatePreviewService;
        this.certificateService = certificateService;
    }

    private X500Name generateX500Name(String name, String surname, String email, String userID, @Nullable String organization,@Nullable String orgUnit){
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, name + " " + surname);
        builder.addRDN(BCStyle.GIVENNAME, name);
        builder.addRDN(BCStyle.SURNAME, surname);
        builder.addRDN(BCStyle.E, email);
        builder.addRDN(BCStyle.UID, userID);
        if(organization != null){
            builder.addRDN(BCStyle.O, organization);
        }
        if(orgUnit != null){
            builder.addRDN(BCStyle.OU, orgUnit);
        }
        return builder.build();
    }

    @Override
    public CertificateRequest createRequest(CertificateRequestIn certificateRequestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        if(user.getUserType().equals("STANDARD") && certificateRequestDTO.getCertificateType() == CertificateType.ROOT){
            throw new BadRequestException("Standard user cannot request root certificate");
        }

        Certificate parent = certificatePreviewService.get(certificateRequestDTO.getParentCertificateId());
        CertificateRequest certificateRequest = new CertificateRequest();

        certificateRequest.setParent(parent);
        certificateRequest.setCertificateType(certificateRequestDTO.getCertificateType());
        certificateRequest.setCreationTime(LocalDateTime.now());
        certificateRequest.setOwner(user);

        CertificateRequest certificateRequestSaved = certificateRequestRepository.save(certificateRequest);

        X500Name subject = generateX500Name(user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getId().toString(),
                certificateRequestDTO.getOrganization(),
                certificateRequestDTO.getOrganizationUnit());
        if(user.getUserType().equals("ADMIN") || Objects.equals(user.getId(), parent.getUser().getId()) ){
            try {
                CertificatePair pair = certificateService.createCertificate(parent.getId(), subject, 30, user, certificateRequestDTO.getCertificateType());
                certificateRequest.setIsAccepted(true);
                certificateRequest.setLinkedCertificate(pair.getCertPreview());
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        }

        return certificateRequestSaved;
    }

    @Override
    public CertificateRequest update(CertificateRequest request) {
        return certificateRequestRepository.save(request);
    }
}
