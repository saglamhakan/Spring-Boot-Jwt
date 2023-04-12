package security.springbootjwt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenManager {

    private static final String secretKey = "HaydiKodlayalım";

    private static final int validity = 5 * 60 * 1000; // ms cinsinden olduğu için böyle yazdık. bu 5dk demek

    public String generateToken(String userName) {  //Aldığı userName e göre token oluşturur.
        return Jwts.builder()
                .setSubject(userName)
                .setIssuer("haydiKodlayalım") //kim tarafından oluşturulduğu
                .setIssuedAt(new Date(System.currentTimeMillis())) //Hangi tarihte oluşturuldu, currentTimeMillis : Oluşturulduğu an imzalandı.
                .setExpiration(new Date(System.currentTimeMillis() + validity)) // ne zamana kadar geçerli
                .signWith(SignatureAlgorithm.HS256, secretKey) //Şifreleme
                .compact();
    }
    //. dan sonra başlayanların adı claim dir

    public boolean tokenValidate(String token) {  //token ın validate(doğru) olup olmadığını kontrol eder, süresi geçmiş mi vs.
        if (getUserNameToken(token) != null && isExpired(token)){
            return true;
        }
        return false;
    }

    public String getUserNameToken(String token) {  //hangi userın tokenı olduğunu bulacak
        Claims claims = getClaims(token);
        return claims.getSubject();

    }

    public boolean isExpired(String token) {  //hangi userın tokenı olduğunu bulacak
        Claims claims = getClaims(token); //claimsleri getirmesi için
        return claims.getExpiration().after(new Date(System.currentTimeMillis())); //Şu anki andan sonraysa
    }

    private static Claims getClaims(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody(); //claimsleri getirmesi için
        return claims;
    }
}