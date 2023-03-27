package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.model.Certificate;

public interface CertificatePreviewService {
    Certificate insert(Certificate certificate);

    Certificate get(Integer id);
}
