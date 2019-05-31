package pl.simplemethod.codebin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import pl.simplemethod.codebin.githubOauth.GithubClient;
import pl.simplemethod.codebin.srv.SrvClient;
import pl.simplemethod.codebin.linkDeploy.LinkClient;

@SpringBootApplication
@EntityScan("pl.simplemethod.codebin")


public class CodebinApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodebinApplication.class, args);
    }

    @Bean
    public LinkClient linkClient()
    {
        return new LinkClient("qYR+CHfZmuEc3aHFX&ACAtudr3xUFX!k");
    }

    @Bean
    public SrvClient srvClient() {
        return new SrvClient("http://srv.simplemethod.io:4815");
    }

    @Bean
    public GithubClient githubClient() {
        return new GithubClient("2f5c2010372081b036ff", "29e08d58c97f1448d3bd16f2ac10e5541be53937");
    }

    @Bean
    public String string() {
        return "";
    }

}
