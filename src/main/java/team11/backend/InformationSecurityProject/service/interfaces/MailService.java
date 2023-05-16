package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.model.AccountActivationMethod;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.model.VerificationCodeType;

public interface MailService {
    void sendVerificationCode(String contact, Integer verificationCode, AccountActivationMethod method, VerificationCodeType type, String url);
    void sendActivation(User user, Integer activationCode, AccountActivationMethod method, String url);
}
