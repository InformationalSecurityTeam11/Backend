package team11.backend.InformationSecurityProject.service;

import team11.backend.InformationSecurityProject.dto.CertificateRequestIn;
import team11.backend.InformationSecurityProject.model.CertificateRequest;

public interface CertificateRequestService {
    CertificateRequest createRequest(CertificateRequestIn certificateRequestDTO);
}
