package com.SSproject.classes.filter;

import com.SSproject.classes.service.JWTService;
import com.SSproject.classes.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Filtraggio della richiesta: {}");

        // Verifico se è una richiesta pubblica (login o authenticate)
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/login") || requestURI.contains("/authenticateLogin") || requestURI.contains("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verifico se c'è un token nella sessione
        HttpSession session = request.getSession(false);
        String token = null;
        if (session != null) {
            token = (String) session.getAttribute("jwt_token");
        }

        // Se non c'è un token nella sessione, provo a cercarlo nell'header Authorization
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        String username = null;
        if (token != null) {
            username = jwtService.extractUserName(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Username trovato: {} - Procedimento con l'autenticazione");

            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                logger.info("Token valido per l'utente: {}");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.warn("Token non valido per l'utente: {}");

                // Se il token non è valido e siamo in un contesto web con sessione, rimuovi il token
                if (session != null) {
                    session.removeAttribute("jwt_token");
                    // Opzionale: redirectt alla pagina di login
                    response.sendRedirect("/login");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
