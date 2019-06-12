package pl.simplemethod.codebin.linkDeploy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LinkController {

    @Autowired
    LinkClient linkClient;

    @GetMapping("/share/{ID}")
    public String getDecryptText(@PathVariable(value = "ID") String decryptText, Model model) {
        String port = linkClient.decrypt(decryptText);
        if (port != null) {
            model.addAttribute("port", "http://srv.simplemethod.io:" + linkClient.decrypt(decryptText));
        } else {
            model.addAttribute("error", "Invalid link");
        }
        return "share";
    }
}
