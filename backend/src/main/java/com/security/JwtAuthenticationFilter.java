package com.security;

import io.jsonwebtoken.Claims;
import com.repository.StudentRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final StudentRepository students;

    public JwtAuthenticationFilter(JwtService jwtService, StudentRepository students) {
        this.jwtService = jwtService;
        this.students = students;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            Claims claims;
            try {
                claims = jwtService.parse(header.substring(7));
            } catch (RuntimeException ignored) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            String email = claims.getSubject();
            String tokenRole = claims.get("role", String.class);
            if (email != null && tokenRole != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var account = students.findByEmailIgnoreCase(email).orElse(null);
                if (account != null && tokenRole.equals(account.getRole())) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            account.getEmail(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole())));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
