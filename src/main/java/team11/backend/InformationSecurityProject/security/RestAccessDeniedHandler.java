package team11.backend.InformationSecurityProject.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        OutputStream responseStream = response.getOutputStream();
        responseStream.write("Access Denied!".getBytes());
        responseStream.flush();
    }
}
