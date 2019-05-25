package pl.simplemethod.codebin;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
public class securityConfig extends WebSecurityConfigurerAdapter {
    // TODO: 24.05.2019 Poprawić konfiguracje
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/css/**", "/postlogin", "/v1.0/**")
                .permitAll()
                .antMatchers("/zaloguj")
                .authenticated()
                .and()
                .oauth2Login();
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
