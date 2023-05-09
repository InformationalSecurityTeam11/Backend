package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.model.AccountActivationMethod;
import team11.backend.InformationSecurityProject.model.User;

public interface MailService {
    void sendPasswordReset(User user, Integer activationCode, AccountActivationMethod method);

    void sendActivation(User user, Integer activationCode, AccountActivationMethod method);
}
