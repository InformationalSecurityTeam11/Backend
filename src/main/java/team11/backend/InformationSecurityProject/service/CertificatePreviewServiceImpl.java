package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.repository.CertificateRepository;
import team11.backend.InformationSecurityProject.service.interfaces.CertificatePreviewService;

import java.math.BigInteger;
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
        if(certificateOpt.isEmpty()){
            throw new NotFoundException("Certificate with given serial number does not exist");
        }
        return certificateOpt.get();
    }
}
