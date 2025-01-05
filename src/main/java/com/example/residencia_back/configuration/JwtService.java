/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 05 Ene 2025
 * @date 05/01/2025
 */
package com.example.residencia_back.configuration;

import com.example.residencia_back.entities.administration.Session;
import com.example.residencia_back.helpers.ToolHelper;
import com.example.residencia_back.repositories.administration.SessionRepository;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SecretKey;
    
    @Value("${jwt.sessionLifetime}")
    private Long SessionLifetime;

    @Autowired
    private SessionRepository sesionRepository;

    public String getToken(UserDetails user, String claimName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", claimName);
        return getToken(claims, user);
    }

    private String getToken(Map<String,Object> extraClaims, UserDetails user) {
        Date expirationDate = new Date(System.currentTimeMillis()+ (SessionLifetime * 1_000));
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expirationDate)
            .signWith(getKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    private Key getKey() {
       byte[] keyBytes=Decoders.BASE64.decode(SecretKey);
       return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Boolean valid = false;
        final String username = getUsernameFromToken(token);
        Optional<Session> s = sesionRepository.findByToken(token);
        if (!s.isPresent()){ return valid; }
        Date expired_at = ToolHelper.timeStampToDate(s.get().getFecha_fin());
        if (username.equals(userDetails.getUsername()) && s.get().getActivo() == true && !isTokenExpired(expired_at)) {
            valid = true;
        }
        return valid;
    }

    private Claims getAllClaims(String token)
    {
        return Jwts
            .parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public <T> T getClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims=getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @SuppressWarnings("unused")
    private Date getExpiration(String token)
    {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(Date expired_at)
    {
        return expired_at.before(new Date());
    }
    
}
