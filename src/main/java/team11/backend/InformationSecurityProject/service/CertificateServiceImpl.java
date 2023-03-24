package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.Certificate;
import team11.backend.InformationSecurityProject.repository.CertificateRepository;

import java.util.Optional;

@Service
public class CertificateServiceImpl implements CertificateService{

    private final CertificateRepository certificateRepository;
    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository){

        this.certificateRepository = certificateRepository;
    }

    @Override
    public Certificate get(Integer id) {
        Optional<Certificate> certificateOpt = certificateRepository.findById(id);
        if(certificateOpt.isEmpty()){
            throw new NotFoundException("Certificate with id does not exist");
        }
        return certificateOpt.get();
    }
}
