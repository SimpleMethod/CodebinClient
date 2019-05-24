package pl.simplemethod.codebin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import pl.simplemethod.codebin.githubOauth.githubClient;
import pl.simplemethod.codebin.srv.srvClient;

@SpringBootApplication
@EntityScan("pl.simplemethod.codebin")


public class codebinApplication {

    public static void main(String[] args) {
        SpringApplication.run(codebinApplication.class, args);
    }

    @Bean
    public srvClient srvClient() {
        return new srvClient("http://srv.simplemethod.io:4815");
    }

    @Bean
    public githubClient githubClient() {
        return new githubClient("2f5c2010372081b036ff", "29e08d58c97f1448d3bd16f2ac10e5541be53937");
    }

    @Bean
    public String string() {
        return "";
    }

}
