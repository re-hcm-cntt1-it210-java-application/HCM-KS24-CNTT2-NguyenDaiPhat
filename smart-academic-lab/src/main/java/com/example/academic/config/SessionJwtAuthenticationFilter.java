package com.example.academic.config;

import com.example.academic.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class SessionJwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Duration REFRESH_WINDOW = Duration.ofMinutes(1);

  private final JwtService jwtService;
  private final JwtDecoder jwtDecoder;
  private final Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter;

  public SessionJwtAuthenticationFilter(
          JwtService jwtService,
          JwtDecoder jwtDecoder,
          Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter) {
    this.jwtService = jwtService;
    this.jwtDecoder = jwtDecoder;
    this.jwtAuthenticationConverter = jwtAuthenticationConverter;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    if (SecurityContextHolder.getContext().getAuthentication() == null && !hasBearerToken(request)) {
      HttpSession session = request.getSession(false);
      String token = session == null ? null : (String) session.getAttribute("ACCESS_TOKEN");
      if (token != null) {
        try {
          Jwt jwt = jwtDecoder.decode(token);
          if (shouldRefresh(jwt)) {
            String newToken = jwtService.refreshToken(token);
            session.setAttribute("ACCESS_TOKEN", newToken);
            jwt = jwtDecoder.decode(newToken);
          }

          var authentication = jwtAuthenticationConverter.convert(jwt);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException e) {
          session.invalidate();
          SecurityContextHolder.clearContext();
          if (!isPublicRequest(request)) {
            if (isApiRequest(request)) {
              response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
              response.sendRedirect("/login?expired");
            }
            return;
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean shouldRefresh(Jwt jwt) {
    Instant expiresAt = jwt.getExpiresAt();
    return expiresAt != null && !Instant.now().isBefore(expiresAt.minus(REFRESH_WINDOW));
  }

  private boolean hasBearerToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    return authorization != null && authorization.startsWith("Bearer ");
  }

  private boolean isApiRequest(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/api/");
  }

  private boolean isPublicRequest(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return uri.equals("/")
            || uri.equals("/login")
            || uri.equals("/register")
            || uri.equals("/error")
            || uri.equals("/api/auth/login")
            || uri.startsWith("/css/");
  }
}
