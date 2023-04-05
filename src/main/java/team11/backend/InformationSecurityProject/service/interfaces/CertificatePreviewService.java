package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.service.CertificatePreviewServiceImpl;

import java.math.BigInteger;

public interface CertificatePreviewService {
    Certificate insert(Certificate certificate);

    Certificate get(Integer id);

    Certificate getBySerial(BigInteger serialNumber);

    public CertificateValidationObject validateCertificate(BigInteger serialNumber);

    public class CertificateValidationObject{
        public Certificate certificate;
        public boolean isValid;
        public CertificateValidationObject(Certificate certificate, boolean isValid){
            this.isValid = isValid;
            this.certificate = certificate;
        }
    }
}
