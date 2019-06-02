package pl.simplemethod.codebin.githubOauth;


import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.simplemethod.codebin.model.Containers;
import pl.simplemethod.codebin.model.Users;
import pl.simplemethod.codebin.repository.ContainersRepository;
import pl.simplemethod.codebin.repository.UsersRepository;


@RestController
@RequestMapping("github")
public class GithubRestController {

    @Autowired
    GithubClient githubClient;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    private ContainersRepository containersRepository;

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
    ResponseEntity getReposCloneWithMinimalInfo(@CookieValue("token") String token, @PathVariable(value = "userName") String username, @PathVariable(value = "repoName") String repos) {
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
    ResponseEntity getReposInfo(@CookieValue("token") String token, @PathVariable(value = "userName") String username, @PathVariable(value = "repoName") String repos) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(githubClient.getReposInfo(token, username, repos).toString(), headers, HttpStatus.valueOf(200));
    }

    /**
     * Statistics contributors in a repository. Does not work for private repositories and requires a double request for working. https://developer.github.com/v3/repos/statistics/
     *
     * @param token    Token for authorization
     * @param username User name
     * @param repos    Name of the repository
     * @return Json object with data
     */
    @GetMapping("/repos/{userName}/{repoName}/contributors")
    public @ResponseBody
    ResponseEntity getReposContributorsStatistics(@CookieValue("token") String token, @PathVariable(value = "userName") String username, @PathVariable(value = "repoName") String repos) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(githubClient.getReposContributorsStatistics(token, username, repos).toString(), headers, HttpStatus.valueOf(200));
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
        return new ResponseEntity<>(githubClient.getUserInfo(token).toString(), headers, HttpStatus.valueOf(200));
    }


    /**
     * Getting information from the database about a user
     *
     * @return Json with data
     */
    @GetMapping(value = "/local")
    public @ResponseBody
    ResponseEntity checkUserPremium(@CookieValue("id") String id) {
        org.json.JSONObject body = new org.json.JSONObject();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Users users = usersRepository.getFirstById(Integer.valueOf(id));
        body.put("user_id", users.getId());
        body.put("github_id", users.getGithub());
        if (users.getSubscription() == null) {
            body.put("subscription_status", "");
        } else {
            body.put("subscription_status", users.getSubscription());
        }

        return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(200));
    }

    /**
     * Returns JSON with user information
     *
     * @param token    Token for authorization
     * @param username Username
     * @return Json with data
     */
    @GetMapping(value = "/users/{userName}")
    public @ResponseBody
    ResponseEntity getInfoAboutUser(@CookieValue("token") String token, @PathVariable(value = "userName") String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(githubClient.getUserInfo(token, username).toString(), headers, HttpStatus.valueOf(200));
    }


    /**
     * Method for verifying if a profile exists
     *
     * @param token Token to github authorize
     * @return Json object
     */
    @GetMapping("/user/checktoken")
    public @ResponseBody
    ResponseEntity checkToken(@RequestParam("token") String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        org.json.JSONObject body = new org.json.JSONObject();
        try {
            Users users = usersRepository.getFirstByToken(token);
            if (users == null) {
                body.put("token", "errr");
                return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(404));
            } else {
                org.json.JSONObject oauth = githubClient.getUserInfo(token);
                try {
                    oauth.get("login");
                    if (oauth.get("login") != null) {
                        body.put("token", users);
                        return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(200));
                    }
                } catch (org.json.JSONException e) {
                    body.put("token", "errr");
                    return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(404));
                }


            }
        } catch (org.json.JSONException e) {
            return new ResponseEntity<>(e, headers, HttpStatus.valueOf(404));

        }
        return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(404));
    }

    @GetMapping("/user/repos/public")
    public @ResponseBody
    ResponseEntity getpublicrepos(@CookieValue("token") String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONParser parser = new JSONParser();
        org.json.JSONObject body = new org.json.JSONObject();
        org.json.simple.JSONArray result = new org.json.simple.JSONArray();
        Object obj = null;
        try {
            Users users = usersRepository.getFirstByToken(token);
            if (users == null) {
                body.put("token", "errr");
                return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(404));
            } else {
                try {
                    obj = parser.parse(githubClient.getUserRepos(token));
                    org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) obj;
                    jsonArray.forEach(item -> {
                        org.json.simple.JSONObject obj1 = (org.json.simple.JSONObject) item;

                        if (obj1.get("private").toString().equals("false")) {
                            org.json.simple.JSONObject helper = new org.json.simple.JSONObject();
                            helper.put("language", obj1.get("language"));
                            helper.put("id", obj1.get("id"));
                            helper.put("license", obj1.get("license"));
                            helper.put("name", obj1.get("name"));
                            helper.put("git_url", obj1.get("git_url"));
                            helper.put("html_url", obj1.get("html_url"));
                            helper.put("description", obj1.get("description"));
                            helper.put("created_at", obj1.get("created_at"));

                            Containers containers = containersRepository.getFirstByName(obj1.get("id").toString());
                            if (containers != null) {
                                helper.put("container_create", obj1.get("id"));

                            } else {
                                helper.put("container_create", null);
                            }
                            result.add(helper);
                        }
                    });
                    return new ResponseEntity<>(result.toString(), headers, HttpStatus.valueOf(200));
                } catch (NullPointerException | ParseException | org.json.JSONException e) {
                    body.put("token", "errr");
                    return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(404));
                }
            }
        } catch (org.json.JSONException e) {
            return new ResponseEntity<>(e, headers, HttpStatus.valueOf(404));

        }
    }


    /**
     * Returns information about one repository of a logged user
     *
     * @param token  Token to github authorize
     * @param repoId The identifier of the repository
     * @return Json object
     */
    @GetMapping("/user/repos/{repoId}")
    public @ResponseBody
    ResponseEntity getPublicRepo(@CookieValue("token") String token, @PathVariable(value = "repoId") String repoId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONParser parser = new JSONParser();
        org.json.JSONObject body = new org.json.JSONObject();
        org.json.simple.JSONArray result = new org.json.simple.JSONArray();
        org.json.simple.JSONObject helper = new org.json.simple.JSONObject();
        Object obj = null;
        try {
            Users users = usersRepository.getFirstByToken(token);
            if (users == null) {
                body.put("token", "errr");
                return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(404));
            } else {
                try {
                    obj = parser.parse(githubClient.getUserRepos(token));
                    org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) obj;
                    jsonArray.forEach(item -> {
                        org.json.simple.JSONObject obj1 = (org.json.simple.JSONObject) item;
                        if (obj1.get("private").toString().equals("false") && obj1.get("id").toString().equals(repoId)) {
                            helper.put("language", obj1.get("language"));
                            helper.put("id", obj1.get("id"));
                            helper.put("license", obj1.get("license"));
                            helper.put("name", obj1.get("name"));
                            helper.put("git_url", obj1.get("git_url"));
                            helper.put("html_url", obj1.get("html_url"));
                            helper.put("description", obj1.get("description"));
                            helper.put("created_at", obj1.get("created_at"));
                            Containers containers = containersRepository.getFirstByName(obj1.get("id").toString());
                            if (containers != null) {
                                helper.put("container_create", obj1.get("id"));
                            } else {
                                helper.put("container_create", null);
                            }

                        }
                    });
                    return new ResponseEntity<>(helper.toString(), headers, HttpStatus.valueOf(200));
                } catch (NullPointerException | ParseException | org.json.JSONException e) {
                    body.put("token", "errr");
                    return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(404));
                }
            }
        } catch (org.json.JSONException e) {
            return new ResponseEntity<>(e, headers, HttpStatus.valueOf(404));

        }
    }
}
