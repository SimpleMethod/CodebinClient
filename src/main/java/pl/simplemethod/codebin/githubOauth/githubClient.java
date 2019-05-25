package pl.simplemethod.codebin.githubOauth;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class githubClient {
    private String CLIENT_ID;
    private String CLIENT_SECRET;

    public githubClient(String client_id, String client_secret) {
        this.CLIENT_ID = client_id;
        this.CLIENT_SECRET = client_secret;
    }

    /**
     * The method is used to retrieve the minimum information about the repository.
     *
     * @param token     Token for authorization
     * @param username  User name
     * @param reposName Name of the repository
     * @return Json object with data
     */
    public org.json.JSONObject getCloneReposMinimalInfo(String token, String username, String reposName) {
        JSONParser parser = new JSONParser();
        org.json.JSONObject body = new org.json.JSONObject();
        Object obj = null;
        try {
            HttpResponse<String> reposInfo = Unirest.get("https://api.github.com/repos/" + username + "/" + reposName).header("Authorization", "Bearer " + token).header("Content-Type", "application/json").asString();
            obj = parser.parse(reposInfo.getBody().toString());
            if (obj == null) {
                throw new NullPointerException();
            }
        } catch (NullPointerException | ParseException | UnirestException e) {
            body.put("ok", false);
            body.put("exception", e);
        } finally {
            if (obj != null) {
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
                String url = (String) jsonObject.get("clone_url");
                body.put("clone_url", jsonObject.get("clone_url"));
                body.put("full_name", jsonObject.get("full_name"));
                body.put("private", jsonObject.get("private"));
                body.put("html_url", jsonObject.get("html_url"));
                body.put("description", jsonObject.get("description"));
                body.put("language", jsonObject.get("language"));
                try {
                    if (!url.isEmpty()) {
                        String error = (String) jsonObject.get("error");
                        body.put("error", error);
                    }
                } catch (NullPointerException e) {
                    body.put("error", e);
                }
            }
        }
        return body;
    }

    /**
     * The method is used to retrieve all information about the repository.
     *
     * @param token     Token for authorization
     * @param username  User name
     * @param reposName Name of the repository
     * @return Json object with data
     */
    public String getReposInfo(String token, String username, String reposName) {
        try {
            HttpResponse<String> userInfo = Unirest.get("https://api.github.com/repos/" + username + "/" + reposName).header("Authorization", "Bearer " + token).header("Content-Type", "application/json").asString();
            return userInfo.getBody().toString();
        } catch (UnirestException e) {
            return "error " + e.toString();
        }
    }

    /**
     * Returns the list of user repositories
     *
     * @param token Token for authorization
     * @return Json object with data
     */
    public String getUserRepos(String token) {
        try {
            HttpResponse<String> userReposInfo = Unirest.get("https://api.github.com/user/repos").header("Authorization", "Bearer " + token).header("Content-Type", "application/json").asString();
            return userReposInfo.getBody().toString();
        } catch (UnirestException e) {
            return "error " + e.toString();
        }
    }

    /**
     * Returns information about the user
     *
     * @param token Token for authorization
     * @return Json object with data
     */
    public String getUserInfo(String token) {
        try {
            HttpResponse<String> userInfo = Unirest.get("https://api.github.com/user").header("Authorization", "Bearer " + token).header("Content-Type", "application/json").asString();
            return userInfo.getBody().toString();
        } catch (UnirestException e) {
            return "error " + e.toString();
        }
    }

    /**
     * Creates a token for authorization
     *
     * @param code The code extracted from the github api
     * @return Json object with data
     */
    public org.json.JSONObject getAccessToken(String code) {
        JSONParser parser = new JSONParser();
        org.json.JSONObject body = new org.json.JSONObject();
        Object obj = null;
        try {
            HttpResponse<JsonNode> accessToken = Unirest.get("https://github.com/login/oauth/access_token")
                    .header("accept", "application/json").header("Content-Type", "application/json")
                    .queryString("client_id", CLIENT_ID)
                    .queryString("client_secret", CLIENT_SECRET)
                    .queryString("code", code).asJson();
            obj = parser.parse(accessToken.getBody().toString());
            if (obj == null) {
                throw new NullPointerException();
            }

        } catch (NullPointerException | ParseException | UnirestException e) {
            body.put("ok", false);
            body.put("exception", e);
        } finally {
            if (obj != null) {
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
                String status = (String) jsonObject.get("access_token");
                body.put("access_token", status);
                try {
                    if (!status.isEmpty()) {
                        String error = (String) jsonObject.get("error");
                        body.put("error", error);
                    }
                } catch (NullPointerException e) {
                    body.put("error", e);
                }
            }
        }
        return body;
    }
}
