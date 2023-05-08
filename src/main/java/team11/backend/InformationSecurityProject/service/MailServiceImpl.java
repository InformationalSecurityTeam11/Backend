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
    private void sendEmail(){
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
    private void sendMessage(){

    }
    @Override
    public void sendActivation(User user, Integer activationCode, AccountActivationMethod method) {
        this.user = user;
        this.activationCode = activationCode;
        if(method == AccountActivationMethod.EMAIL){
            sendEmail();
        }else {
            sendMessage();
        }
//        Email from = new Email("information.security.team11@outlook.com");
//
//        Email to = new Email(user.getEmail());
//        Mail mail = new Mail();
//        mail.setFrom(from);
//        Personalization personalization = new Personalization();
//        personalization.addTo(to);
//        personalization.addDynamicTemplateData("url_page","http://localhost:4200/accountActivation&code=" + activationCode);
//        personalization.addDynamicTemplateData("user_name", user.getName());
//        personalization.addDynamicTemplateData("subject","Account activation");
//        mail.addPersonalization(personalization);
//        mail.setTemplateId("d-a8dc11707b5a4320872f2d0a8b093948");
//
//        Request request = new Request();
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            this.mailSender.api(request);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
