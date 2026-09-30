package com.dakartechwallet.walletbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Internal-Api-Key";
    private static final String CHEMIN_PROTEGE = "/api/payments";

    @Value("${internal.api-key}")
    private String cleAttendue;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith(CHEMIN_PROTEGE)) {
            String cleRecue = request.getHeader(HEADER);

            if (cleRecue == null || !cleRecue.equals(cleAttendue)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"erreur\":\"Clé API interne manquante ou invalide\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}