package pl.simplemethod.codebin.srv;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1.0")
public class srvRestController {

    @Autowired
    srvClient srvClient;

    // TODO: 24.05.2019  zmienić na POST

    /**
     * REST for container creation
     *
     * @param dockerImage  Image to be reproduced with tag, example: srv_java:1.0
     * @param exposedPorts Container's internal port (For SRV images use: 8080)
     * @param hostPort     Outside port (port to connect)
     * @param name         Name of container
     * @return Json object with status
     */
    @GetMapping("/srv/create")
    public @ResponseBody
    ResponseEntity getReposCloneWithMinimalInfo(@RequestParam("dockerimage") String dockerImage, @RequestParam("exposedports") Integer exposedPorts, @RequestParam("hostport") Integer hostPort, @RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        org.json.JSONObject response = srvClient.createAndRunContainer(srvClient.generateCreateConfig(dockerImage, exposedPorts, hostPort), name);
        return new ResponseEntity<>(response.toString(), headers, HttpStatus.valueOf(response.getInt("status")));
    }
}
