package pl.simplemethod.codebin.srv;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.simplemethod.codebin.model.Containers;
import pl.simplemethod.codebin.model.Images;
import pl.simplemethod.codebin.repository.ContainersRepository;
import pl.simplemethod.codebin.repository.ImagesRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("srv")
public class SrvRestController {

    @Autowired
    SrvClient srvClient;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ContainersRepository containersRepository;



    @GetMapping("/images1")
    public @ResponseBody
    ResponseEntity kek() {

        List<Containers> containers = new ArrayList<>();
        Images images  = imagesRepository.getFirstByName("sdssd");
        containers.add(new Containers("test","xddd",images,1010,4510,(long)1000,(long)1000,1, Instant.now().getEpochSecond()));
        containers.forEach(containersRepository::save);

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("xd", headers, HttpStatus.valueOf(200));
    }


    /**
     * REST for container creation
     *
     * @param dockerImage  Images to be reproduced with tag, example: srv_java:1.0
     * @param exposedPorts Container's internal port (For SRV images use: 8080)
     * @param name         Name of container
     * @param hostPorts    Outside port (port to connect)
     * @param ramMemory    Maximum amount of allocated RAM
     * @param diskQuota    Maximum amount of allocated memory on the disk
     * @return Json object with data
     */
    @GetMapping("container/create")
    public @ResponseBody
    ResponseEntity createContainer(@RequestParam("dockerimage") String dockerImage, @RequestParam("exposedports") Integer exposedPorts, @RequestParam("hostports") Integer hostPorts, @RequestParam("name") String name, @RequestParam("rammemory") Long ramMemory, @RequestParam("diskquota") Long diskQuota) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        org.json.JSONObject response = srvClient.createAndRunContainer(srvClient.generateCreateConfig(dockerImage, exposedPorts, hostPorts, ramMemory, diskQuota), name);
        int status;
        if (response.get("status").toString().equals("204")) {
            status = 200;
            List<Containers> containers = new ArrayList<>();
            Images images  = imagesRepository.getFirstByName(dockerImage);
            containers.add(new Containers(name,response.get("id").toString(),images,exposedPorts,hostPorts,ramMemory,diskQuota,1, Instant.now().getEpochSecond()));
            containers.forEach(containersRepository::save);
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
    @PostMapping("container/{ID}/restart")
    public @ResponseBody
    ResponseEntity restartContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        org.json.JSONObject restartContainer = srvClient.restartContainer(containerId);
        return new ResponseEntity<>(restartContainer.toString(), headers, HttpStatus.valueOf(restartContainer.getInt("status")));
    }

    /**
     * Stop the container with the specified ID
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @PostMapping("container/{ID}/stop")
    public @ResponseBody
    ResponseEntity stopContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        org.json.JSONObject response = srvClient.stopContainer(containerId);
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(response.getInt("status")));
    }

    /**
     * Start the container with the specified ID
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @PostMapping("container/{ID}/start")
    public @ResponseBody
    ResponseEntity startContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        org.json.JSONObject response = srvClient.startContainer(containerId);
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(response.getInt("status")));
    }

    /**
     * The method returns container logs
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @GetMapping("container/{ID}/logs")
    public @ResponseBody
    ResponseEntity logsContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        String response = srvClient.logsContainer(containerId);
        String replaceString = response.replace('\u0001', '\n');
        replaceString = replaceString.replace('�', ' ');

        return new ResponseEntity<>(replaceString, headers, HttpStatus.valueOf(201));
    }

    /**
     * The method returns information about the container
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @GetMapping("container/{ID}/info")
    public @ResponseBody
    ResponseEntity infoContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String response = srvClient.infoContainer(containerId);
        return new ResponseEntity<>(response, headers, HttpStatus.valueOf(200));
    }

    /**
     * The method returns information about the docker server
     *
     * @return Json object as response
     */
    @GetMapping("info")
    public @ResponseBody
    ResponseEntity infoDocker() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String response = srvClient.infoDocker();
        return new ResponseEntity<>(response, headers, HttpStatus.valueOf(200));
    }

    /**
     * Method delete the container
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @DeleteMapping("container/{ID}/delete")
    public @ResponseBody
    ResponseEntity deleteContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
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
    @PostMapping("container/{ID}/exec")
    public @ResponseBody
    ResponseEntity execContainer(@PathVariable(value = "ID") String containerId, @RequestParam("path") String path, @RequestParam("argument") String arguments) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        org.json.JSONObject response = srvClient.execContainer(containerId, path, arguments);
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(response.getInt("status")));
    }

}
