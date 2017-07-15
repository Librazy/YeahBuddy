package cn.edu.xmu.yeahbuddy.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class MainController {

    @RequestMapping("/204")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void empty() {
    }
}