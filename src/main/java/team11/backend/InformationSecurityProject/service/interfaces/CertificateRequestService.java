package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.dto.CertificateRequestIn;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.model.CertificateRequest;

public interface CertificateRequestService {
    CertificateRequest createRequest(CertificateRequestIn certificateRequestDTO);

    CertificateRequest update(CertificateRequest request);

    Boolean approve(int id);
    Boolean reject(int id);
}
