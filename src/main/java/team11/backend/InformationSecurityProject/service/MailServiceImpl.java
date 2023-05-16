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
import team11.backend.InformationSecurityProject.model.VerificationCodeType;
import team11.backend.InformationSecurityProject.service.interfaces.MailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.sendinblue.api-key}")
    private String API_KEY;

    @Autowired
    public MailServiceImpl(){
    }

    private void sendAccountActivationMessage(String phoneNumber){
        // TODO send activation code using WhatsApp or SMS
    }
    private void sendPasswordResetMessage(String phoneNumber){
        // TODO send password reset code using WhatsApp or SMS
    }
    private void sendLoginConfirmationMessage(String phoneNumber){
        // TODO send login confirmation code using WhatsApp or SMS
    }
    private void sendEmail(String email, long templateID, Integer verificationCode, String url){
        ApiClient mailClient = Configuration.getDefaultApiClient();
        mailClient.setApiKey(API_KEY);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
        SendSmtpEmail emailSender = new SendSmtpEmail();
        SendSmtpEmailTo receiver = (new SendSmtpEmailTo()).email(email);

        emailSender.setTo(List.of(receiver));
        Map<String, String> params = new HashMap<>();
        params.put("VERIFICATION_URL", url + "?code=" + verificationCode.toString());
        params.put("VERIFICATION_CODE", verificationCode.toString());

        emailSender.params(params);
        emailSender.templateId(templateID);
        try {
            apiInstance.sendTransacEmail(emailSender);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendVerificationCode(String contact, Integer verificationCode, AccountActivationMethod method, VerificationCodeType type, String url) {
        long templateID;
        Consumer<String> messageSender;
        if(type == VerificationCodeType.LOGIN){
            templateID = 4;
            messageSender = this::sendLoginConfirmationMessage;
        } else {
            templateID = 3;
            messageSender = this::sendPasswordResetMessage;
        }

        if(method == AccountActivationMethod.EMAIL){
            sendEmail(contact, templateID, verificationCode, url);
        }else {
            messageSender.accept(contact);
        }
    }

    @Override
    public void sendActivation(User user, Integer activationCode, AccountActivationMethod method, String url) {
        if(method == AccountActivationMethod.EMAIL){
            sendEmail(user.getEmail(),1, activationCode, url);
        }else {
            sendAccountActivationMessage(user.getTelephoneNumber());
        }
    }
}
