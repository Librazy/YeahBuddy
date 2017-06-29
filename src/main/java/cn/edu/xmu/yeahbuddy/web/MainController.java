package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Team;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @PreAuthorize("hasAuthority('ViewReport')")
    @RequestMapping("/admin")
    public String admin(
            Model model) {
        String name = ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
        model.addAttribute("name", name);
        return "admin";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @RequestMapping("/team/login")
    public String teamLogin() {
        return "team/login";
    }

    @RequestMapping("/team/login-error")
    public String teamLoginError(Model model) {
        model.addAttribute("loginError", true);
        return "team/login";
    }

    @RequestMapping("/team")
    public String team(
            Model model) {
        String name = ((Team) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        model.addAttribute("name", name);
        return "team/index";
    }
}