package pl.simplemethod.codebin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll();
     /*   http.authorizeRequests()
                .antMatchers("/", "/**", "/postlogin", "/v1.0/**", "/404", "/logowanie/github")
                .permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .oauth2Login();
                /*
      */
        // http://localhost/logowanie/github
        http.oauth2Login()
                .authorizationEndpoint()
                .baseUri("/logowanie")
                .authorizationRequestRepository(authorizationRequestRepository()).and().permitAll();
    }

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }
}