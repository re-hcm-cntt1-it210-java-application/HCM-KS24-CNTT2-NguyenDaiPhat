package com.example.academic.config;

import com.example.academic.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.HexFormat;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  public static final String[] PUBLIC_ENDPOINTS = {
          "/", "/login", "/register", "/css/**", "/error", "/api/auth/login"
  };

  @Value("${jwt.signer-key}")
  private String signerKey;

  @Bean
  public SecurityFilterChain filterChain(
          HttpSecurity http,
          JwtDecoder jwtDecoder,
          Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter,
          SessionJwtAuthenticationFilter sessionJwtAuthenticationFilter) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                    .requestMatchers("/student/**").hasRole("STUDENT")
                    .requestMatchers("/lecturer/**").hasRole("LECTURER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/profile/**", "/logout", "/api/auth/logout", "/api/auth/refresh").authenticated() // chỉ cần login
                    .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, authException) -> {
                      if (request.getRequestURI().startsWith("/api/")) {
                        response.sendError(401);
                      } else {
                        response.sendRedirect("/login");
                      }
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(403)) // không đủ quyền
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .decoder(jwtDecoder)
                            .jwtAuthenticationConverter(jwtAuthenticationConverter)
                    )
            )
            .addFilterBefore(sessionJwtAuthenticationFilter, BearerTokenAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public JwtDecoder jwtDecoder(JwtService jwtService) {
    SecretKeySpec secretKey = new SecretKeySpec(HexFormat.of().parseHex(signerKey), "HmacSHA256");
    NimbusJwtDecoder delegate = NimbusJwtDecoder.withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();

    return token -> {
      jwtService.verifyToken(token);
      return delegate.decode(token);
    };
  }

  @Bean
  public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    return jwt -> {
      String role = jwt.getClaimAsString("role");
      List<SimpleGrantedAuthority> authorities = role == null
              ? List.of()
              : List.of(new SimpleGrantedAuthority("ROLE_" + role));
      return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    };
  }

  @Bean
  public SessionJwtAuthenticationFilter sessionJwtAuthenticationFilter(
          JwtService jwtService,
          JwtDecoder jwtDecoder,
          Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter) {
    return new SessionJwtAuthenticationFilter(jwtService, jwtDecoder, jwtAuthenticationConverter);
  }
}
