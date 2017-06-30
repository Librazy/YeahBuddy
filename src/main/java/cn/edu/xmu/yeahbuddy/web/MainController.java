package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @PreAuthorize("hasAuthority('ViewReport')")
    @RequestMapping("/admin")
    public String admin(Model model) {
        String name = ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
        model.addAttribute("name", name);
        return "admin";
    }

    @RequestMapping("/login")
    public String login(@RequestAttribute(required = false) String error, Model model) {
        if(error != null){
            model.addAttribute("loginError", true);
        }
        return "login";
    }

    @RequestMapping("/team/login")
    public String teamLogin(@RequestAttribute(required = false) String error, Model model) {
        if(error != null){
            model.addAttribute("loginError", true);
        }
        return "team/login";
    }

    @RequestMapping({"/team", "/team/"})
    @PreAuthorize("authenticated")
    public String team(Model model) {
        String name = ((Team) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        model.addAttribute("name", name);
        return "team/index";
    }

    @RequestMapping("/tutor/login")
    public String tutorLogin(@RequestAttribute(required = false) String error, Model model) {
        if(error != null){
            model.addAttribute("loginError", true);
        }
        return "tutor/login";
    }

    @RequestMapping({"/tutor", "/tutor/"})
    @PreAuthorize("authenticated")
    public String tutor(Model model) {
        String name = ((Tutor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPhone();
        model.addAttribute("name", name);
        return "tutor/index";
    }
}