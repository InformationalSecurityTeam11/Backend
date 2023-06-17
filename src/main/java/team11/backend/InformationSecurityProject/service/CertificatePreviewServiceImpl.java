package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.repository.CRLRepository;
import team11.backend.InformationSecurityProject.repository.CertificateRepository;
import team11.backend.InformationSecurityProject.service.interfaces.CertificatePreviewService;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CertificatePreviewServiceImpl implements CertificatePreviewService {

    private final CertificateRepository certificateRepository;
    @Autowired
    public CertificatePreviewServiceImpl(CertificateRepository certificateRepository){

        this.certificateRepository = certificateRepository;
    }
    @Override
    public Certificate insert(Certificate certificate){
        return certificateRepository.save(certificate);
    }
    @Override
    public Certificate get(Integer id) {
        Optional<Certificate> certificateOpt = certificateRepository.findById(id);
        if(certificateOpt.isEmpty()){
            throw new NotFoundException("Certificate with id does not exist");
        }
        return certificateOpt.get();
    }
    @Override
    public Certificate getBySerial(BigInteger serialNumber){
        Optional<Certificate> certificateOpt = certificateRepository.findCertificateBySerialNumber(serialNumber);
        return certificateOpt.orElse(null);
    }


    @Override
    public CertificateValidationObject validateCertificate(BigInteger serialNumber) {

        Certificate certificate = getBySerial(serialNumber);
        if (certificate == null) {
            return null;
        }

        if (certificate.getRevoke() != null) {
            return new CertificateValidationObject(certificate, false);
        }
        LocalDate currentDate = LocalDateTime.now().toLocalDate();
        LocalDate startDate = certificate.getStartDate().toLocalDate();
        LocalDate expireDate = certificate.getExpireDate().toLocalDate();
        if ((startDate.isBefore(currentDate) || startDate.isEqual(currentDate)) &&
                (expireDate.isAfter(currentDate) || expireDate.isEqual(currentDate))) {
            return new CertificateValidationObject(certificate, true);
        }
        return new CertificateValidationObject(certificate, false);
    }

    public boolean verifyUploadedCertificate(InputStream inputStream) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            BigInteger serialNumber = certificate.getSerialNumber();
            Certificate cer = this.certificateRepository.findCertificateBySerialNumber(serialNumber).orElse(null);
           if (cer != null)
            if (cer.getRevoke() != null)
                return false;
            certificate.checkValidity();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
