package team11.backend.InformationSecurityProject;

import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import team11.backend.InformationSecurityProject.dto.SubjectInfoDTO;
import team11.backend.InformationSecurityProject.repository.KeyStoreRepository;
import team11.backend.InformationSecurityProject.service.CertificateService;
import team11.backend.InformationSecurityProject.utils.CertificateUtility;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

@SpringBootApplication
public class InformationSecurityProjectApplication {

	public static void main(String[] args)
	{
		SpringApplication.run(InformationSecurityProjectApplication.class, args);
	}

}
