package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team11.backend.InformationSecurityProject.exceptions.BadRequestException;
import team11.backend.InformationSecurityProject.exceptions.NotFoundException;
import team11.backend.InformationSecurityProject.model.AccountActivation;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.repository.AccountActivationRepository;
import team11.backend.InformationSecurityProject.repository.UserRepository;
import team11.backend.InformationSecurityProject.service.interfaces.AccountActivationService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountActivationServiceImpl implements AccountActivationService {

    private final AccountActivationRepository accountActivationRepository;
    private final UserRepository userRepository;
    @Autowired
    public AccountActivationServiceImpl(AccountActivationRepository accountActivationRepository, UserRepository userRepository){

        this.accountActivationRepository = accountActivationRepository;
        this.userRepository = userRepository;
    }
    @Override
    public AccountActivation insert(AccountActivation activation) {
        return accountActivationRepository.save(activation);
    }

    @Override
    public void activateAccount(Integer activationId){
        Optional<AccountActivation> accountActivationOpt = accountActivationRepository.findById(activationId);
        if (accountActivationOpt.isPresent()){
            AccountActivation userActivation = accountActivationOpt.get();
            User user = userActivation.getUser();

            if (userActivation.getDateCreated().plus(userActivation.getLifeSpan()).isAfter(LocalDateTime.now())
                    && userActivation.isValid()){
                user.setEnabled(true);
                userRepository.save(user);
                userActivation.setValid(false);
                accountActivationRepository.save(userActivation);
                return;
            }
            accountActivationRepository.delete(userActivation);
            userRepository.delete(user);
            throw new BadRequestException("Activation expired. Register again!");
        }
        else{
            throw new NotFoundException("Account with given activation code does not exist!");
        }
    }
}
