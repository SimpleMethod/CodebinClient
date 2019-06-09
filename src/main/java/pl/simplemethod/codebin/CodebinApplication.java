package pl.simplemethod.codebin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import pl.simplemethod.codebin.githubOauth.GithubClient;
import pl.simplemethod.codebin.linkDeploy.LinkClient;
import pl.simplemethod.codebin.srv.SrvClient;

@SpringBootApplication
@EntityScan("pl.simplemethod.codebin")
public class CodebinApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CodebinApplication.class);
        application.setAdditionalProfiles("ssl");
        application.run(args);
    }

    @Bean
    public LinkClient linkClient()
    {
        return new LinkClient("qYR+CHfZmuEc3aHF");
    }

    @Bean
    public SrvClient srvClient() {
        return new SrvClient("https://simplemethod.io:8433");
    }

    @Bean
    public GithubClient githubClient() {
        return new GithubClient("2f5c2010372081b036ff", "29e08d58c97f1448d3bd16f2ac10e5541be53937");
    }

    @Bean
    public String string()
    {
        return "";
    }
}
