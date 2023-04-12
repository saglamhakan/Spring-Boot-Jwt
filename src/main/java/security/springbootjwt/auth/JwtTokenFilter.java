package security.springbootjwt.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenManager tokenManager;

    public JwtTokenFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = httpServletRequest.getHeader("Authorization");
        String username = null;
        String token = null;

        if (authHeader != null && authHeader.contains("Bearer ")) { //Bearer içeriyorsa onu çek
            token = authHeader.substring(7);//7 harften sonraki kısmı ver bearer
            try {
                username = tokenManager.getUserNameToken(token);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if (username != null && token != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {  //SecurityContextHolder sisteme daha önce giriş yapmamış demek
                if (tokenManager.tokenValidate(token)) {
                    UsernamePasswordAuthenticationToken upassToken = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                    upassToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(upassToken);
                }

            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }

    }
}
