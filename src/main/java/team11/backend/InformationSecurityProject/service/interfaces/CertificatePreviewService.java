package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.model.Certificate;

import java.math.BigInteger;

public interface CertificatePreviewService {
    Certificate insert(Certificate certificate);

    Certificate get(Integer id);

    Certificate getBySerial(BigInteger serialNumber);

    public Certificate validateCertificate(BigInteger serialNumber);
}
