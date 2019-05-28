package pl.simplemethod.codebin.githubOauth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("github")
public class GithubRestController {

    @Autowired
    GithubClient githubClient;

    /**
     * Returns the necessary information about the repository
     *
     * @param token    Token for authorization
     * @param username Username
     * @param repos    Name of the repository
     * @return Json with data
     */
    @GetMapping("/repos/{userName}/{repoName}/minimal")
    public @ResponseBody
    ResponseEntity getReposCloneWithMinimalInfo(@CookieValue("token") String token,@PathVariable(value="userName") String username, @PathVariable(value="repoName") String repos) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
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
    @GetMapping("/repos/{userName}/{repoName}")
    public @ResponseBody
    ResponseEntity getReposInfo(@CookieValue("token") String token, @PathVariable(value="userName") String username, @PathVariable(value="repoName") String repos) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(githubClient.getReposInfo(token, username, repos), headers, HttpStatus.valueOf(200));
    }

    /**
     * Statistics contributors in a repository. Does not work for private repositories and requires a double request for working. https://developer.github.com/v3/repos/statistics/
     * @param token     Token for authorization
     * @param username  User name
     * @param repos Name of the repository
     * @return Json object with data
     */
    @GetMapping("/repos/{userName}/{repoName}/contributors")
    public @ResponseBody
    ResponseEntity getReposContributorsStatistics(@CookieValue("token") String token, @PathVariable(value="userName") String username, @PathVariable(value="repoName") String repos) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(githubClient.getReposContributorsStatistics(token, username, repos), headers, HttpStatus.valueOf(200));
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
        headers.setContentType(MediaType.APPLICATION_JSON);
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
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(githubClient.getUserInfo(token), headers, HttpStatus.valueOf(200));
    }
    /**
     * Returns JSON with user information
     *
     * @param token Token for authorization
     *  @param username Username
     * @return Json with data
     */
    @GetMapping(value = "/users/{userName}")
    public @ResponseBody
    ResponseEntity getInfoAboutUser(@CookieValue("token") String token, @PathVariable(value="userName") String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(githubClient.getUserInfo(token,username), headers, HttpStatus.valueOf(200));
    }
}
