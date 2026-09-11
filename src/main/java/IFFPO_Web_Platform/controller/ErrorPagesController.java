package IFFPO_Web_Platform.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPagesController {

    @GetMapping("/admin/access-refuse")
    public String accessRefuse(){
        return "admin/access-refuse";
    }

}
