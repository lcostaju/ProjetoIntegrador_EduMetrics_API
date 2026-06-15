package br.edu.iftm.edumetrics.security;

import br.edu.iftm.edumetrics.estruturas.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filtro HTTP que aplica o Rate Limiter a todos os endpoints
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired private RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String clienteId = extrairClienteId(request);

        if (!rateLimiter.permitir(clienteId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                {"erro": "Rate limit excedido", "limite": 100, "janela": "60s"}
                """);
            return;
        }

        chain.doFilter(request, response);
    }

    private String extrairClienteId(HttpServletRequest request) {
        // Tenta X-Forwarded-For (proxy/load balancer); cai no IP direto
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded != null ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}