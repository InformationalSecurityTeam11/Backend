package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.dto.CertificateRequestIn;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.model.CertificateRequest;
import team11.backend.InformationSecurityProject.model.User;

import java.util.List;

public interface CertificateRequestService {
    CertificateRequest createRequest(CertificateRequestIn certificateRequestDTO);
    CertificateRequest update(CertificateRequest request);


    List<CertificateRequest> getAll();

    Boolean approve(int id);
    Boolean reject(int id, String reason);
    List<CertificateRequest> getCertificateRequestByOwner(User owner);
}
