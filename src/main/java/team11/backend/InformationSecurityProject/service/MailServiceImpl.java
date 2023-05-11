package team11.backend.InformationSecurityProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailTo;
import team11.backend.InformationSecurityProject.model.AccountActivationMethod;
import team11.backend.InformationSecurityProject.model.User;
import team11.backend.InformationSecurityProject.service.interfaces.MailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.sendinblue.api-key}")
    private String API_KEY;

    private User user;
    private Integer activationCode;
    @Autowired
    public MailServiceImpl(){
    }
    private void sendActivationEmail(){
        ApiClient mailClient = Configuration.getDefaultApiClient();
        mailClient.setApiKey(API_KEY);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
        SendSmtpEmail email = new SendSmtpEmail();
        SendSmtpEmailTo receiver = (new SendSmtpEmailTo()).email(user.getEmail());

        email.setTo(List.of(receiver));
        Map<String, String> params = new HashMap<>();
        params.put("ACTIVATION_URL", "http://localhost:4200/accountActivation&code=" + activationCode.toString());
        params.put("ACTIVATION_CODE", activationCode.toString());

        email.params(params);
        email.templateId(1L);
        try {
            CreateSmtpEmail result = apiInstance.sendTransacEmail(email);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void sendActivationMessage(){
        // TODO send activation code using WhatsApp or SMS
    }

    private void sendPasswordResetEmail(String email){
        ApiClient mailClient = Configuration.getDefaultApiClient();
        mailClient.setApiKey(API_KEY);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
        SendSmtpEmail emailSender = new SendSmtpEmail();
        SendSmtpEmailTo receiver = (new SendSmtpEmailTo()).email(email);

        emailSender.setTo(List.of(receiver));
        Map<String, String> params = new HashMap<>();
        params.put("RESET_URL", "http://localhost:4200/passwordReset&code=" + activationCode.toString());
        params.put("RESET_CODE", activationCode.toString());

        emailSender.params(params);
        emailSender.templateId(3L);
        try {
            CreateSmtpEmail result = apiInstance.sendTransacEmail(emailSender);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendPasswordResetMessage(String phoneNumber){
        // TODO send password reset code using WhatsApp or SMS
    }
    @Override
    public void sendPasswordReset(String contact, Integer activationCode, AccountActivationMethod method){
        this.activationCode = activationCode;
        if(method == AccountActivationMethod.EMAIL){
            sendPasswordResetEmail(contact);
        }else {
            sendPasswordResetMessage(contact);
        }
    }
    @Override
    public void sendActivation(User user, Integer activationCode, AccountActivationMethod method) {
        this.user = user;
        this.activationCode = activationCode;
        if(method == AccountActivationMethod.EMAIL){
            sendActivationEmail();
        }else {
            sendActivationMessage();
        }
    }
}
