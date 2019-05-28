package pl.simplemethod.codebin.srv;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1.0")
public class SrvRestController {

    @Autowired
    SrvClient srvClient;

    // TODO: 24.05.2019  zmienić na POST, delete etc.


    /**
     * REST for container creation
     *
     * @param dockerImage  Image to be reproduced with tag, example: srv_java:1.0
     * @param exposedPorts Container's internal port (For SRV images use: 8080)
     * @param name
     * @param hostPort     Outside port (port to connect)
     * @param ramMemory    Maximum amount of allocated RAM
     * @param diskQuota    Maximum amount of allocated memory on the disk
     * @return Json object with data
     */
    @GetMapping("/srv/container/create")
    public @ResponseBody
    ResponseEntity createContainer(@RequestParam("dockerimage") String dockerImage, @RequestParam("exposedports") Integer exposedPorts, @RequestParam("hostport") Integer hostPort, @RequestParam("name") String name, @RequestParam("rammemory") Long ramMemory, @RequestParam("diskquota") Long diskQuota) {
        HttpHeaders headers = new HttpHeaders();
        org.json.JSONObject response = srvClient.createAndRunContainer(srvClient.generateCreateConfig(dockerImage, exposedPorts, hostPort, ramMemory, diskQuota), name);
        int status = 200;
        if (response.get("status").toString().equals("204")) {
            status = 200;
        } else {
            status = Integer.valueOf(response.get("status").toString());
        }
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(status));
    }

    /**
     * The method restarts the container
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @PostMapping("/srv/container/{ID}/restart")
    public @ResponseBody
    ResponseEntity restartContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        org.json.JSONObject restartContainer = srvClient.restartContainer(containerId);
        return new ResponseEntity<>(restartContainer.toString(), headers, HttpStatus.valueOf(restartContainer.getInt("status")));
    }

    /**
     * @param containerId Container ID
     * @return Json object as response
     */
    @PostMapping("/srv/container/{ID}/stop")
    public @ResponseBody
    ResponseEntity stopContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        org.json.JSONObject response = srvClient.stopContainer(containerId);
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(response.getInt("status")));
    }

    /**
     * Start the container with the specified ID
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @PostMapping("/srv/container/{ID}/start")
    public @ResponseBody
    ResponseEntity startContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        org.json.JSONObject response = srvClient.startContainer(containerId);
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(response.getInt("status")));
    }

    /**
     * The method returns container logs
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @GetMapping("/srv/container/{ID}/logs")
    public @ResponseBody
    ResponseEntity logsContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        String response = srvClient.logsContainer(containerId);
        String replaceString = response.replace('\u0001', '\n');
        replaceString = replaceString.replace('�', ' ');

        return new ResponseEntity<>(replaceString, headers, HttpStatus.valueOf(201));
    }

    /**
     * Method delete the container
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @DeleteMapping("/srv/container/{ID}/delete")
    public @ResponseBody
    ResponseEntity deleteContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        org.json.JSONObject response = srvClient.deleteContainer(containerId);
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(response.getInt("status")));
    }

    /**
     * Method execute command on the container
     *
     * @param containerId Container ID
     * @param path        Path to the bash script or package
     * @param arguments   Argument to execute
     * @return Json object as response
     */
    @PostMapping("/srv/container/{ID}/exec")
    public @ResponseBody
    ResponseEntity execContainer(@PathVariable(value = "ID") String containerId, @RequestParam("path") String path, @RequestParam("argument") String arguments) {
        HttpHeaders headers = new HttpHeaders();
        org.json.JSONObject response = srvClient.execContainer(containerId, path, arguments);
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(response.getInt("status")));
    }

}
