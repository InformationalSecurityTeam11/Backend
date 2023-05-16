package team11.backend.InformationSecurityProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team11.backend.InformationSecurityProject.model.VerificationCode;
import team11.backend.InformationSecurityProject.model.VerificationCodeType;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
    Optional<VerificationCode> findByCodeAndType(Integer code, VerificationCodeType type);
}
