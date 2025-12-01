package com.nhnacademy.byeol23backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@Component
public class JwtParser {
    private final RSAPublicKey publicKey;

    public JwtParser(@Value("${jwt.public-key}") String pem) {
        try {
            this.publicKey = RSAPublicKeyParser.parse(pem);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("JWT 공개키 초기화 실패");
        }
    }

    public Claims parseToken(String token) {
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parser()
                .verifyWith(publicKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
