package pl.simplemethod.codebin.githubOauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.simplemethod.codebin.model.Users;
import pl.simplemethod.codebin.repository.UsersRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PostLogin {

    @Autowired
    GithubClient githubClient;

    @Autowired
    UsersRepository usersRepository;


    /**
     * Authorizes and saves a token in a cookie.
     *
     * @param response HttpServletResponse to save cookies
     * @param code     Authorization code received from Github
     * @return Code html
     */
    @GetMapping("/postlogin")
    public @ResponseBody
    ResponseEntity postlogin(HttpServletResponse response, @RequestParam("code") String code) {
        HttpHeaders headers = new HttpHeaders();
        String token, githubId;
        List<Users> usersList = new ArrayList<>();
        try {

            token = githubClient.getAccessToken(code).get("access_token").toString();
            githubId = githubClient.getUserInfo(token).get("id").toString();
            Users users = usersRepository.getFirstByGithub(Integer.valueOf(githubId));
            if (users == null) {
                users = new Users(token, Integer.valueOf(githubId), "user", null, null);
                usersList.add(users);
                usersList.forEach(usersRepository::save);
            }
            else
            {
                users.setToken(token);
                usersRepository.save(users);
            }
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(999999999);

            Cookie idCookie = new Cookie("id", Integer.toString(users.getId()));
            idCookie.setMaxAge(999999999);

            response.addCookie(idCookie);
            response.addCookie(cookie);
        } catch (org.json.JSONException e) {
            return new ResponseEntity<>(e, headers, HttpStatus.valueOf(404));
        }

        return new ResponseEntity<>("<script type=\"text/javascript\">window.location.href = \"/\"</script>", headers, HttpStatus.valueOf(200));
    }
}
