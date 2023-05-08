package team11.backend.InformationSecurityProject.service.interfaces;

import team11.backend.InformationSecurityProject.model.AccountActivation;

public interface AccountActivationService {
    AccountActivation insert(AccountActivation activation);

    void activateAccount(Integer activationId);
}
