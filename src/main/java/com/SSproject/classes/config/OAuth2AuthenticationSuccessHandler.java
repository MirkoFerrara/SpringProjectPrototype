package com.SSproject.classes.config;

import com.SSproject.classes.service.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTService jwtService;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(JWTService jwtService) {
        this.jwtService = jwtService;
        setDefaultTargetUrl("/home");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();

        // Estrai informazioni dall'utente OAuth2 (variano per provider)
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = null;
        String name = null;

        // Gestisci diversi provider
        if (oauthToken.getAuthorizedClientRegistrationId().equals("google")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if (oauthToken.getAuthorizedClientRegistrationId().equals("github")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("login");  // GitHub usa 'login' come username
        }

        // Genera un token JWT per l'utente OAuth2
        // Puoi usare l'email come username, o creare un username basato sul provider
        String username = email != null ? email : name + "-" + oauthToken.getAuthorizedClientRegistrationId();
        String token = jwtService.generateToken(username);

        // Salva il token nella sessione
        HttpSession session = request.getSession();
        session.setAttribute("jwt_token", token);
        session.setAttribute("username", username);

        // Prosegui con il redirect standard
        super.onAuthenticationSuccess(request, response, authentication);
    }
}