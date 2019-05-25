package pl.simplemethod.codebin.githubOauth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("v1.0")
public class githubRestController {

    @Autowired
    githubClient githubClient;

    /**
     * Returns the necessary information about the repository
     *
     * @param token    Token for authorization
     * @param username Username
     * @param repos    Name of the repository
     * @return Json with data
     */
    @GetMapping("/repos/minimal")
    public @ResponseBody
    ResponseEntity getReposCloneWithMinimalInfo(@CookieValue("token") String token, @RequestParam("username") String username, @RequestParam("repos") String repos) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(githubClient.getCloneReposMinimalInfo(token, username, repos).toString(), headers, HttpStatus.valueOf(200));
    }

    /**
     * Returns all information about the repository
     *
     * @param token    Token for authorization
     * @param username Username
     * @param repos    Name of the repository
     * @return Json with data
     */
    @GetMapping("/repos")
    public @ResponseBody
    ResponseEntity getReposInfo(@CookieValue("token") String token, @RequestParam("username") String username, @RequestParam("repos") String repos) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(githubClient.getReposInfo(token, username, repos), headers, HttpStatus.valueOf(200));
    }

    /**
     * Returns a list of repositories with information about them
     *
     * @param token Token for authorization
     * @return Json with data
     */
    @GetMapping(value = "/user/repos")
    public @ResponseBody
    ResponseEntity repos(@CookieValue("token") String token) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(githubClient.getUserRepos(token), headers, HttpStatus.valueOf(200));
    }

    /**
     * Returns JSON with user information
     *
     * @param token Token for authorization
     * @return Json with data
     */
    @GetMapping(value = "/user")
    public @ResponseBody
    ResponseEntity getInfoAboutOwner(@CookieValue("token") String token) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(githubClient.getUserInfo(token), headers, HttpStatus.valueOf(200));
    }
    /**
     * Returns JSON with user information
     *
     * @param token Token for authorization
     *  @param username Username
     * @return Json with data
     */
    @GetMapping(value = "/users")
    public @ResponseBody
    ResponseEntity getInfoAboutUser(@CookieValue("token") String token, @RequestParam("username") String username) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(githubClient.getUserInfo(token,username), headers, HttpStatus.valueOf(200));
    }
}
