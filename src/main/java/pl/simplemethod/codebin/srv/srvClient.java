package pl.simplemethod.codebin.srv;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

@Component
public class srvClient {
    private String SERVER_URL;

    public srvClient(String serverUrl) {
        this.SERVER_URL = serverUrl;
    }


    private org.json.JSONObject jsonHelper(HttpResponse<JsonNode> response) {
        org.json.JSONObject body = new org.json.JSONObject();
        JSONParser parser = new JSONParser();
        Object obj = null;

        try {
            obj = parser.parse(response.getBody().toString());
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
            body.put("message", jsonObject.get("message"));
            body.put("status", String.valueOf(response.getStatus()));
            return body;
        } catch (NullPointerException | ParseException | org.json.JSONException e) {
            body.put("message", "Something went wrong:" + e);
            body.put("status", 510);
            return body;
        }
    }

    /**
     * Method to generate JSON used to create docker container
     *
     * @param dockerImage  Image to be reproduced with tag, example: srv_java:1.0
     * @param exposedPorts Container's internal port (For SRV images use: 8080)
     * @param hostPort     Outside port (port to connect)
     */
    public org.json.JSONObject generateCreateConfig(String dockerImage, Integer exposedPorts, Integer hostPort) {
        org.json.JSONObject body = new org.json.JSONObject();
        body.put("Image", dockerImage);
        body.put("ExposedPorts", new org.json.JSONObject().put(exposedPorts + "/tcp", new org.json.JSONObject()));
        body.put("HostConfig", new org.json.JSONObject().put("PortBindings", new org.json.JSONObject().put(exposedPorts + "/tcp", new org.json.JSONArray().put(0, new org.json.JSONObject().put("HostPort", hostPort.toString())))));
        body.put("RestartPolicy", new org.json.JSONObject().put("Name", "always"));
        System.out.println(body.toString());
        return body;
    }

    /**
     * The method creates and activates a container of the given specification
     *
     * @param dockerConfig Docker configuration in JSON format
     * @param name         Name of container
     * @return Json object with status
     */
    public org.json.JSONObject createAndRunContainer(org.json.JSONObject dockerConfig, String name) {
        JSONParser parser = new JSONParser();
        org.json.JSONObject body = new org.json.JSONObject();
        Object obj = null;
        try {
            HttpResponse<JsonNode> createContainer = Unirest.post(SERVER_URL + "/v1.0/containers/create")
                    .header("accept", "application/json").queryString("name", name).header("Content-Type", "application/json").body(dockerConfig).asJson();
            obj = parser.parse(createContainer.getBody().toString());
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
            body.put("messageBuilder",jsonObject.get("message") );
            //body.put("status",String.valueOf(createContainer.getStatus()));
            //System.out.println(body.toString());
            if (obj == null) {
                throw new NullPointerException();
            }
        } catch (NullPointerException | ParseException | org.json.JSONException | UnirestException e) {
            body.put("message", "Cannot connect to the server");
            body.put("status", 510);
        } finally {
            if (obj != null) {
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
                try {
                    String id = (String) jsonObject.get("Id");
                    body.put("Id", id);
                    if (!id.isEmpty()) {
                        org.json.JSONObject start  = startContainer(id);
                        body.put("status",start.get("status"));
                    } else {
                        String error = (String) jsonObject.get("error");
                        body.put("message", error);
                        body.put("status", 510);
                    }
                } catch (NullPointerException e) {
                    body.put("message", e);
                    body.put("status", 510);
                }
            }
        }
        System.out.println("Dane: "+ body.toString());
        return body;
    }

    /**
     * Start the container with the specified ID
     *
     * @param id Container ID
     * @return Json object with status
     */
    public org.json.JSONObject startContainer(String id) {
        org.json.JSONObject body = new org.json.JSONObject();
        try {
            HttpResponse<JsonNode> startContainer = Unirest.post(SERVER_URL + "/v1.0/containers/" + id + "/start")
                    .header("accept", "application/json").header("Content-Type", "application/json").asJson();
            //body.put("id", id);
            body.put("status", String.valueOf(startContainer.getStatus()));
            if (startContainer.getBody() != null) {
                body.put("message", "Something went wrong");
                body.put("status", 510);
            } else {
                System.out.println("Start to się wykonuje? w else"+body.toString());
                return body;
            }
            System.out.println("Start to się wykonuje? w else poza if"+body.toString());
            return body;
        } catch (UnirestException e) {
            body.put("message", "Something went wrong");
            body.put("status", 510);
            return body;
        }
    }

    /**
     * Method stops the container
     *
     * @param id Container ID
     * @return Json object with status
     */
    public org.json.JSONObject stopContainer(String id) {
        org.json.JSONObject body = new org.json.JSONObject();
        try {
            HttpResponse<JsonNode> stopContainer = Unirest.post(SERVER_URL + "/v1.0/containers/" + id + "/stop").header("accept", "application/json").header("Content-Type", "application/json").asJson();
            if (stopContainer.getStatus() != 204) {
                body.put("message", "Something went wrong ");
                body.put("status", stopContainer.getStatus());
                return body;
            } else {
                body.put("status", stopContainer.getStatus());
                return body;
            }
        } catch (NullPointerException | org.json.JSONException | UnirestException e) {
            body.put("message", "Something went wrong: " + e);
            body.put("status", 510);
            return body;
        }
    }

    /**
     * Method delete the container
     *
     * @param id Container ID
     * @return Json object with status
     */
    public org.json.JSONObject deleteContainer(String id) {
        org.json.JSONObject body = new org.json.JSONObject();
        try {
            HttpResponse<JsonNode> deleteContainer = Unirest.delete(SERVER_URL + "/v1.0/containers/" + id).header("accept", "application/json").header("Content-Type", "application/json").asJson();
            if (deleteContainer.getStatus() != 204) {
                body.put("message", "Something went wrong ");
                body.put("status", deleteContainer.getStatus());
                return body;
            } else {
                body.put("status", deleteContainer.getStatus());
                return body;
            }
        } catch (NullPointerException | org.json.JSONException | UnirestException e) {
            body.put("message", "Something went wrong: " + e);
            body.put("status", 510);
            return body;
        }
    }

    /**
     * Method execute command on the container
     *
     * @param id        Container ID
     * @param path      Path to the bash script or package
     * @param arguments Argument to execute
     * @return Json object with status
     */
    public org.json.JSONObject execContainer(String id, String path, String arguments) {
        org.json.JSONObject body = new org.json.JSONObject();
        try {
            HttpResponse<JsonNode> execContainer = Unirest.post(SERVER_URL + "/v1.0/containers/" + id + "/exec")
                    .header("accept", "application/json").header("Content-Type", "application/json").queryString("path", path).queryString("argument", arguments).asJson();
            if (execContainer.getStatus() != 200) {
                body.put("message", "Something went wrong ");
                body.put("status", execContainer.getStatus());
                return body;
            } else {
                body.put("status", execContainer.getStatus());
                return body;
            }
        } catch (NullPointerException | org.json.JSONException | UnirestException e) {
            body.put("message", "Something went wrong: " + e);
            body.put("status", 510);
            return body;
        }
    }

    /**
     * The method restarts the container
     *
     * @param id Container ID
     * @return Json object with status
     */
    public org.json.JSONObject restartContainer(String id) {
        org.json.JSONObject body = new org.json.JSONObject();
        try {
            HttpResponse<JsonNode> restartContainer = Unirest.post(SERVER_URL + "/v1.0/containers/" + id + "/restart").header("accept", "application/json").header("Content-Type", "application/json").asJson();
            if (restartContainer.getStatus() != 204) {
                body.put("message", "Something went wrong ");
                body.put("status", restartContainer.getStatus());
                return body;
            } else {
                body.put("status", restartContainer.getStatus());
                return body;
            }
        } catch (NullPointerException | org.json.JSONException | UnirestException e) {
            body.put("message", "Something went wrong: " + e);
            body.put("status", 510);
            return body;
        }
    }

    /**
     * The method returns container logs
     *
     * @param id Container ID
     * @return Json object with status
     */
    public String logsContainer(String id) {
        org.json.JSONObject body = new org.json.JSONObject();
        try {
            HttpResponse<String> logsContainer = Unirest.get(SERVER_URL + "/v1.0/containers/" + id + "/logs")
                    .header("accept", "application/json").header("Content-Type", "application/json").asString();
            if (logsContainer.getStatus() != 200) {
                body.put("message", "Something went wrong ");
                body.put("status", logsContainer.getStatus());
                return body.toString();
            } else {
                return logsContainer.getBody();
            }

        } catch (NullPointerException | org.json.JSONException | UnirestException e) {
            body.put("message", "Something went wrong " + e);
            body.put("status", 510);
            return body.toString();
        }
    }

}
