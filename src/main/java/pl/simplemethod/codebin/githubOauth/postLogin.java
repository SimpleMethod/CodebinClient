package pl.simplemethod.codebin.githubOauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
public class postLogin {

    @Autowired
    githubClient githubClient;

    // Authorization
    @GetMapping("/postlogin")
    public @ResponseBody
    ResponseEntity postlogin(HttpServletResponse response, @RequestParam("code") String code) {
        HttpHeaders headers = new HttpHeaders();
        String token = null;
        try {
            token = (String) githubClient.getAccessToken(code).get("access_token");
        } catch (org.json.JSONException e) {
            return new ResponseEntity<>(e, headers, HttpStatus.valueOf(404));
        }

        // TODO: 24.05.2019  Przrobić na CookieManager
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(999999999);
        response.addCookie(cookie);

        return new ResponseEntity<>("<script type=\"text/javascript\">window.location.href = \"/\"</script>", headers, HttpStatus.valueOf(200));
    }
}
