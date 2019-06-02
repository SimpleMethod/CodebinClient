package pl.simplemethod.codebin.paypal;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client.app}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    /**
     * Creating API context
     * @return Created API context from provided client id and client secret
     */
    @Bean
    public APIContext getContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}
