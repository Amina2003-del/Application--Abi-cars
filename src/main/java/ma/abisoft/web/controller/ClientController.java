package ma.abisoft.web.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;

@Controller
public class ClientController {

    private static final String SESSION_USER_ATTR = "user1";

    @RequestMapping(value = {"/Clientes/rechercher-reserver", "/Administrateur/tableaubord", "/Owner/dashbord"})
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        HttpSession session = request.getSession(false);

        if (session == null) {
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        User user = (User) session.getAttribute("user1");
        if (user == null) {
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        Collection<Role> roles = user.getRoles();

        if (roles == null || roles.isEmpty()) {
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        Role firstRole = roles.iterator().next();
        String roleName = firstRole.getName().toUpperCase();

        switch (roleName) {
            case "CLIENT":
                modelAndView.setViewName("/Clientes/rechercher-reserver");
                break;
            case "ADMIN":
                modelAndView.setViewName("/Administrateur/tableaubord");
                break;
            case "OWNER":
                modelAndView.setViewName("/Owner/dashbord");
                break;
            default:
                modelAndView.setViewName("redirect:/login");
                break;
        }

        modelAndView.addObject("user", user);
        System.out.println("Utilisateur connecté : " + user.getEmail() + " avec rôle " + roleName);

        return modelAndView;
    }
}
