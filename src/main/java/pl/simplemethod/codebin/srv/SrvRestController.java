package pl.simplemethod.codebin.srv;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.simplemethod.codebin.linkDeploy.LinkClient;
import pl.simplemethod.codebin.model.Containers;
import pl.simplemethod.codebin.model.Images;
import pl.simplemethod.codebin.model.Users;
import pl.simplemethod.codebin.repository.ContainersRepository;
import pl.simplemethod.codebin.repository.ImagesRepository;
import pl.simplemethod.codebin.repository.UsersRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("srv")
public class SrvRestController {

    @Autowired
    private SrvClient srvClient;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ContainersRepository containersRepository;

    @Autowired
    private LinkClient linkClient;

    @Autowired
    private UsersRepository usersRepository;

    /**
     * Check if server port is taken and if so, change it to another port
     * @param hostPorts Original server port
     * @return Server port
     */
    private Integer checkPort(Integer hostPorts)
    {
        Containers containers = containersRepository.getByHostPorts(hostPorts);
        Random rand = new Random();
        try
        {
            if(containers.getHostPorts()!=null)
            {
                if(containers.getHostPorts().equals(hostPorts))
                {
                    return checkPort(hostPorts + rand.nextInt((9999 - 1) + 1) + 1);
                }
                else
                {
                    return hostPorts;
                }
            }
            else
            {
                return hostPorts;
            }
        }
        catch (NullPointerException e)
        {
            return hostPorts;
        }
    }


    /**
     * Method is used to get data on the container from the database.
     * @param hostPort Server port
     * @return Object JSON with data
     */
    @GetMapping("users/container/info/{hostPort}")
    public @ResponseBody
    ResponseEntity checkPorts(@PathVariable(value = "hostPort") String hostPort) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        org.json.JSONObject body = new org.json.JSONObject();

        Containers containers = containersRepository.getByHostPorts(Integer.valueOf(hostPort));
        try{
           if(containers.getId()!=null)
           {
               body.put("name", containers.getName());
               body.put("docker_id", containers.getIdDocker());
               body.put("exposed_ports", containers.getExposedPorts());
               body.put("host_ports", containers.getHostPorts());
               body.put("disk_quota", containers.getDiskQuota());
               body.put("ram_memory",containers.getRamMemory());
               body.put("share_url",containers.getShareUrl());
               body.put("create_time", containers.getCreateTime());
               return new ResponseEntity<>(body.toString(),httpHeaders,HttpStatus.valueOf(200));
           }
           else
           {
               body.put("error","Not found");
               return new ResponseEntity<>(body.toString(),httpHeaders,HttpStatus.valueOf(404));
           }
        }
        catch (NullPointerException e)
        {
            body.put("error",e);
            return new ResponseEntity<>(body.toString(),httpHeaders,HttpStatus.valueOf(404));
        }
    }

    /**
     * List of containers of any user
     *
     * @param id User ID
     * @return Object Json with data
     */
    @GetMapping("users/{ID}/container")
    public @ResponseBody
    ResponseEntity usersContainer(@PathVariable(value = "ID") String id) {
        Users users = usersRepository.getFirstById(Integer.valueOf(id));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<Containers> containers = users.getContainers();
        org.json.JSONObject result = new org.json.JSONObject();
        result.put("containers", containers);
        return new ResponseEntity<>(result.toString(), headers, HttpStatus.valueOf(200));
    }

    /**
     * List of containers for login user
     *
     * @param id Current logged user
     * @return Object Json with data
     */
    @GetMapping("user/container")
    public @ResponseBody
    ResponseEntity userContainer(@CookieValue("id") String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Users users = usersRepository.getFirstById(Integer.valueOf(id));
        List<Containers> containers = users.getContainers();
        org.json.JSONObject result = new org.json.JSONObject();
        result.put("containers", containers);
        return new ResponseEntity<>(result.toString(), headers, HttpStatus.valueOf(200));
    }

    /**
     * Returns container information from the database
     *
     * @param dockerGithubId Name of container
     * @return Object Json with data
     */
    @GetMapping("user/container/info")
    public @ResponseBody
    ResponseEntity userContainerCheck(@RequestParam("dockergithubid") String dockerGithubId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Containers containers = containersRepository.getFirstByName(dockerGithubId);
        if (containers != null) {

            return new ResponseEntity<>(containers, headers, HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity<>("error", headers, HttpStatus.valueOf(400));
        }
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
    @PostMapping("container/create")
    public @ResponseBody
    ResponseEntity createContainerForUser(@RequestParam("dockerimage") String dockerImage, @RequestParam("exposedports") Integer exposedPorts, @RequestParam("hostports") Integer hostPorts, @RequestParam("name") String name, @RequestParam("rammemory") Long ramMemory, @RequestParam("diskquota") Long diskQuota, @CookieValue("id") String id, @RequestParam("premiumstatus") Integer premiumStatus, @RequestParam("giturl") String gitUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Images images = imagesRepository.getFirstById(Integer.valueOf(dockerImage));
        hostPorts=checkPort(hostPorts);

        org.json.JSONObject response = srvClient.createAndRunContainer(srvClient.generateCreateConfig(images.getName(), exposedPorts, hostPorts, ramMemory, diskQuota), name);
        int status;
        if (response.get("status").toString().equals("204")) {
            status = 200;
            List<Containers> containers = new ArrayList<>();

            Containers newContainer = new Containers(name, response.get("id").toString(), images, exposedPorts, hostPorts, ramMemory, diskQuota, linkClient.encrypt(String.valueOf(hostPorts)), premiumStatus, Instant.now().getEpochSecond());
            containers.add(newContainer);
            containers.forEach(containersRepository::save);
            try {
                Users users = usersRepository.getFirstById(Integer.parseInt(id));
                users.getContainers().add(newContainer);
                usersRepository.save(users);
            } catch (NumberFormatException e) {
                return new ResponseEntity<>(e.toString(), headers, HttpStatus.NOT_FOUND);
            }

            try {
                Thread.sleep(4000);
                Containers containers1 = containersRepository.getFirstByName(name);
                response.put("exec", srvClient.execContainer(containers1.getIdDocker(), images.getExec(), gitUrl));
            } catch (InterruptedException e) {

            }


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
     * The method returns information about processes inside container
     *
     * @param containerId Container ID
     * @return Json object as response
     */
    @GetMapping("container/{ID}/top")
    public @ResponseBody
    ResponseEntity topContainer(@PathVariable(value = "ID") String containerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String response = srvClient.topContainer(containerId);
        return new ResponseEntity<>(response, headers, HttpStatus.valueOf(200));
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
        headers.setContentType(MediaType.APPLICATION_JSON);
        String response = srvClient.logsContainer(containerId);
        String replaceString = response.replace('\u0001', '\n');
        replaceString = replaceString.replace('�', ' ');

        org.json.JSONObject result = new org.json.JSONObject();
        result.put("logs", replaceString);
        return new ResponseEntity<>(result.toString(), headers, HttpStatus.valueOf(201));
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


        Users users = usersRepository.findByContainersidDocker(containerId);
        Containers containerToDelete = containersRepository.getFirstByIdDocker(containerId);
        users.getContainers().remove(containerToDelete);
        usersRepository.save(users);


        org.json.JSONObject response = srvClient.deleteContainer(containerId);
        containersRepository.removeByIdDocker(containerId);
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
