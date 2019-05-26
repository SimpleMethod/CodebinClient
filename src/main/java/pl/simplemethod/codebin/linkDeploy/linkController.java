package pl.simplemethod.codebin.linkDeploy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class linkController {

@Autowired
linkClient linkClient;

    @GetMapping("/share/{ID}")
    public String getDecryptText(@PathVariable(value="ID") String decryptText, Model model) {
        model.addAttribute("port",  "http://waw1.simplemethod.io:"+linkClient.decrypt(decryptText));
        return "share";
    }
}
