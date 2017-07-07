package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class MainController {

    @NonNls
    private static Log log = LogFactory.getLog(MainController.class);

    @PreAuthorize("hasAuthority('ViewReport')")
    @RequestMapping("/admin")
    public String admin(Model model) {
        String name = ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
        log.debug("Administrator " + name + " viewed /admin");
        model.addAttribute("name", name);
        return "admin";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "login";
    }

    @RequestMapping(value = "/team/login", method = RequestMethod.GET)
    public String teamLogin(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "team/login";
    }

    @RequestMapping({"/team", "/team/"})
    @PreAuthorize("hasRole('TEAM')")
    public String team(Model model) {
        String email = ((Team) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        model.addAttribute("name", email);
        return "team/index";
    }

    @RequestMapping(value = "/tutor/login", method = RequestMethod.GET)
    public String tutorLogin(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "tutor/login";
    }

    @RequestMapping({"/tutor", "/tutor/"})
    @PreAuthorize("hasRole('TUTOR')")
    public String tutor(Model model) {
        String phone = ((Tutor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPhone();
        model.addAttribute("name", phone);
        return "tutor/index";
    }

    @RequestMapping("/204")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void empty() {
    }
}