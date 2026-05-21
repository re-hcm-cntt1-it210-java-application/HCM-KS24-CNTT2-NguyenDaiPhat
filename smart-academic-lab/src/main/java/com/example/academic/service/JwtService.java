package com.example.academic.service;

import com.example.academic.entity.InvalidatedToken;
import com.example.academic.entity.User;
import com.example.academic.repository.InvalidatedTokenRepository;
import com.example.academic.repository.UserRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
  public static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(2);
  public static final Duration REFRESHED_TOKEN_TTL = Duration.ofMinutes(15);
  private static final Duration REFRESH_WINDOW = Duration.ofMinutes(1);

  private final InvalidatedTokenRepository invalidatedTokenRepository;
  private final UserRepository userRepository;

  @Value("${jwt.signer-key}")
  private String signerKey;

  public String generateToken(User user) {
    return generateToken(user, ACCESS_TOKEN_TTL);
  }

  public String generateToken(User user, Duration ttl) {
    try {
      Instant now = Instant.now();
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
              .subject(user.getUsername())
              .issuer("smart-academic-lab")
              .issueTime(Date.from(now))
              .expirationTime(Date.from(now.plus(ttl)))
              .jwtID(UUID.randomUUID().toString())
              .claim("userId", user.getId())
              .claim("role", user.getRole().name())
              .build();

      SignedJWT signedJWT = new SignedJWT(
              new JWSHeader(JWSAlgorithm.HS256),
              claimsSet
      );
      signedJWT.sign(new MACSigner(secretBytes()));
      return signedJWT.serialize();
    } catch (Exception e) {
      throw new IllegalStateException("Cannot generate JWT token", e);
    }
  }

  public SignedJWT verifyToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      boolean verified = signedJWT.verify(new MACVerifier(secretBytes()));
      if (!verified) {
        throw new JwtException("Token signature is invalid");
      }

      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      Date expirationTime = claimsSet.getExpirationTime();
      // ktra 5p
      if (expirationTime == null || expirationTime.toInstant().isBefore(Instant.now())) {
        throw new JwtException("Token is expired");
      }

      String jwtId = claimsSet.getJWTID();
      if (jwtId != null && invalidatedTokenRepository.existsById(jwtId)) {
        throw new JwtException("Token was logged out");
      }

      return signedJWT;
    } catch (JwtException e) {
      throw e;
    } catch (Exception e) {
      throw new JwtException("Token is invalid", e);
    }
  }

  @Transactional
  public void logout(String token) {
    SignedJWT signedJWT = verifyToken(token);
    saveInvalidatedToken(signedJWT);
  }

  @Transactional
  public String refreshToken(String oldToken) {
    SignedJWT signedJWT = verifyToken(oldToken);
    try {
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      Instant expiryTime = claimsSet.getExpirationTime().toInstant();
      Instant refreshFrom = expiryTime.minus(REFRESH_WINDOW);
      if (Instant.now().isBefore(refreshFrom)) {
        throw new IllegalArgumentException("Only refresh token when it has 1 minute or less left");
      }

      User user = userRepository.findByUsername(claimsSet.getSubject())
              .orElseThrow(() -> new IllegalArgumentException("User does not exist"));
      saveInvalidatedToken(signedJWT);
      return generateToken(user, REFRESHED_TOKEN_TTL);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new JwtException("Cannot refresh token", e);
    }
  }

  public long accessTokenSeconds() {
    return ACCESS_TOKEN_TTL.toSeconds();
  }

  public long refreshedTokenSeconds() {
    return REFRESHED_TOKEN_TTL.toSeconds();
  }

  private void saveInvalidatedToken(SignedJWT signedJWT) {
    try {
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      String jwtId = claimsSet.getJWTID();
      Date expirationTime = claimsSet.getExpirationTime();
      if (jwtId == null || expirationTime == null) {
        return;
      }

      invalidatedTokenRepository.save(InvalidatedToken.builder()
              .id(jwtId)
              .expiryTime(expirationTime.toInstant())
              .build());
    } catch (Exception e) {
      throw new JwtException("Cannot invalidate token", e);
    }
  }

  private byte[] secretBytes() {
    return HexFormat.of().parseHex(signerKey);
  }
}
