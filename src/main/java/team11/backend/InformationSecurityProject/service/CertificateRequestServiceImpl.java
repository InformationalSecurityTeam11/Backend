package team11.backend.InformationSecurityProject.service;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.dto.CertificateRequestIn;
import team11.backend.InformationSecurityProject.dto.RequestInfoDTO;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.exceptions.ForbiddenException;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.*;
import team11.backend.InformationSecurityProject.repository.CertificateRequestRepository;
import team11.backend.InformationSecurityProject.repository.UserRepository;
import team11.backend.InformationSecurityProject.service.interfaces.CertificatePreviewService;
import team11.backend.InformationSecurityProject.service.interfaces.CertificateRequestService;
import team11.backend.InformationSecurityProject.service.interfaces.ICertificateService;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class CertificateRequestServiceImpl implements CertificateRequestService {

    private final CertificateRequestRepository certificateRequestRepository;
    private final CertificatePreviewService certificatePreviewService;
    private final ICertificateService certificateService;
    private final UserRepository userRepository;


    @Autowired
    public CertificateRequestServiceImpl(CertificateRequestRepository certificateRequestRepository, CertificatePreviewService certificatePreviewService, ICertificateService certificateService, UserRepository userRepository){

        this.certificateRequestRepository = certificateRequestRepository;
        this.certificatePreviewService = certificatePreviewService;
        this.certificateService = certificateService;
        this.userRepository = userRepository;
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
            throw new ForbiddenException("Standard user cannot request root certificate");
        }
        BigInteger caSerial;
        Certificate parent;
        if(certificateRequestDTO.getCertificateType() == CertificateType.ROOT){
            caSerial = null;
            parent = null;
        }else {
            parent = certificatePreviewService.getBySerial(certificateRequestDTO.getParentCertificateSerialNumber());
            if(parent.getType() == CertificateType.ROOT || parent.getType() == CertificateType.INTERMEDIATE){
                caSerial = certificateRequestDTO.getParentCertificateSerialNumber();
            }else {
                throw new ForbiddenException("Given certificate cannot issue new certificates");
            }
        }

        CertificateRequest certificateRequest = new CertificateRequest();

        certificateRequest.setParent(parent);
        certificateRequest.setCertificateType(certificateRequestDTO.getCertificateType());
        certificateRequest.setCreationTime(LocalDateTime.now());
        certificateRequest.setRequestState(RequestState.PENDING);
        certificateRequest.setOrganization(certificateRequestDTO.getOrganization());
        certificateRequest.setOrganizationUnit(certificateRequestDTO.getOrganizationUnit());
        certificateRequest.setOwner(user);

        if(user.getUserType().equals("ADMIN") || Objects.equals(user.getId(), Objects.requireNonNull(parent).getUser().getId()) ){
            X500Name subject = generateX500Name(user.getName(),
                    user.getSurname(),
                    user.getEmail(),
                    user.getId().toString(),
                    certificateRequestDTO.getOrganization(),
                    certificateRequestDTO.getOrganizationUnit());
            try {
                X509Certificate certificate = certificateService.createCertificate(caSerial, subject, 30);
                certificateRequest.setRequestState(RequestState.APPROVED);
                certificateRequest.setAcceptanceTime(LocalDateTime.now());

                Certificate certPreview = new Certificate();
                certPreview.setType(certificateRequestDTO.getCertificateType());
                certPreview.setUser(user);
                certPreview.setStartDate(
                        certificate.getNotBefore()
                                .toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                );
                certPreview.setExpireDate(
                        certificate.getNotAfter()
                                .toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                );
                certPreview.setSerialNumber(certificate.getSerialNumber());

                certificateRequest.setLinkedCertificate(certificatePreviewService.insert(certPreview));
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        return certificateRequestRepository.save(certificateRequest);
    }

    @Override
    public CertificateRequest update(CertificateRequest request) {
        return certificateRequestRepository.save(request);
    }

    @Override
    public List<CertificateRequest> getAll(){
        return certificateRequestRepository.findAll();
    }

    @Override
    public Set<RequestInfoDTO> getRequestsDTOS(List<CertificateRequest> requests) {
        if(requests.size() == 0)
            return new HashSet<>();
        Set<RequestInfoDTO> certificateRequests = new HashSet<>();
        for(CertificateRequest r : requests){
            certificateRequests.add(new RequestInfoDTO(r, r.getOwner()));
        }
        return certificateRequests;

    }

    @Override
    public List<CertificateRequest> getPendingRequestsForSpecificUser(User user) {
        ArrayList<CertificateRequest> certificateRequests = new ArrayList<>();
        for (CertificateRequest request : this.certificateRequestRepository.findAll()) {
            if (request.getParent() == null)
                continue;
            if (request.getRequestState() != RequestState.PENDING )
                continue;
            if (Objects.equals(request.getParent().getUser().getId(), user.getId())) {
                certificateRequests.add(request);
            }
        }
        return certificateRequests;
    }

    @Override
    public void approve(int id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Optional<CertificateRequest> certificateRequestOpt =  certificateRequestRepository.findById(id);
        if(certificateRequestOpt.isEmpty()){
            throw new NotFoundException("Request with given id not found");
        }
        CertificateRequest certificateRequest = certificateRequestOpt.get();

        User owner = certificateRequest.getOwner();
        if(!certificateRequest.getRequestState().equals(RequestState.PENDING)){
            throw new BadRequestException("This certificate has already been processed");
        }

        if (certificateRequest.getParent().getUser().getId().equals(user.getId()) || user.getUserType().equals("ADMIN")){
            BigInteger parentSerial = certificateRequest.getParent().getSerialNumber();
            X500Name subject = generateX500Name(owner.getName(),
                    owner.getSurname(),
                    owner.getEmail(),
                    owner.getId().toString(),
                    certificateRequest.getOrganization(),
                    certificateRequest.getOrganizationUnit());
            try {
                X509Certificate certificate = certificateService.createCertificate(parentSerial, subject, 30);

                Certificate certPreview = new Certificate();
                certPreview.setType(certificateRequest.getCertificateType());
                certPreview.setUser(certificateRequest.getOwner());
                certPreview.setStartDate(
                        certificate.getNotBefore()
                                .toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                );
                certPreview.setExpireDate(
                        certificate.getNotAfter()
                                .toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                );
                certPreview.setSerialNumber(certificate.getSerialNumber());

                certificateRequest.setLinkedCertificate(certificatePreviewService.insert(certPreview));

            }catch (Exception e){
                throw new BadRequestException(e.getMessage());
            }
            certificateRequest.setRequestState(RequestState.APPROVED);
            certificateRequest.setAcceptanceTime(LocalDateTime.now());
            certificateRequestRepository.save(certificateRequest);

        }else{
            throw new ForbiddenException("You can not approve requests of other users");
        }

    }

    @Override
    public void reject(int id, String reason){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Optional<CertificateRequest> certificateRequestOpt =  certificateRequestRepository.findById(id);
        if(certificateRequestOpt.isEmpty()){
            throw new NotFoundException("Request with given id not found");
        }

        CertificateRequest certificateRequest = certificateRequestOpt.get();

        if(!certificateRequest.getRequestState().equals(RequestState.PENDING)){
            throw new BadRequestException("This certificate has already been processed");
        }
        if (certificateRequest.getParent().getUser().getId().equals(user.getId()) || user.getUserType().equals("ADMIN")){
            certificateRequest.setRequestState(RequestState.REJECTED);
            certificateRequest.setRejection_reason(reason);
            certificateRequest.setAcceptanceTime(LocalDateTime.now());
            certificateRequestRepository.save(certificateRequest);

        }else{
            throw new ForbiddenException("You can not reject requests of other users");
        }
    }


    @Override
    public List<CertificateRequest> getCertificateRequestByOwner(User owner) {
        User user = this.userRepository.findUserByEmail(owner.getEmail()).orElse(null);
        return this.certificateRequestRepository.getCertificateRequestByOwner(user);
    }

}
