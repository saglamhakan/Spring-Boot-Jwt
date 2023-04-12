package security.springbootjwt.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // tüm konfigürasyonlardan önce security i devreye aldık
@AllArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private final JwtTokenFilter jwtTokenFilter;

    @Autowired
    private final UserDetailsService userDetailsService;


    @Autowired
    public void configurePasswordEncoder(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(getBCryptPasswordEncoder());

    }

    @Bean
    BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()//güvenliği devre dışı bırakır
                .authorizeRequests().antMatchers("/login").authenticated()//login dışındaki requestlerde authorization istesin
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//Userın kullanıcı bilgilerini ve şifresini hafızada tutmak için kullanılır. Örneğin aynı sitede sayfalar arası geçiş yaparken tekrar login yapması gerekmez.

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class); //Her loginden önce filtreden geçmesi için
    }
}
