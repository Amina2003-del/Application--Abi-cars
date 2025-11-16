package ma.abisoft.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	
	 // @GetMapping("/") public String root() { return "index"; }
	
	   @GetMapping("/")
	    public String redirectToPublicHome() {
	        return "redirect:/Siteoffeciel/index";
	    }
    @GetMapping("/project-item")
    public String userIndex() {
        return "project-item";
    }

    @GetMapping("/blog-item")
    public String getserviceMaia() {
        return "blog-item";
    }
    @GetMapping("/produits")
    public String getproduitMaia() {
        return "produits";
    }
    @GetMapping("/contact-us")
    public String getcontactas() {
        return "contact-us";
    }
    @GetMapping("/blog")
    public String getressourceMaia() {
        return "blog";
    }
    @GetMapping("/about-us")
    public String getcentreAide() {
        return "about-us";
    }
    @GetMapping("/services")
    public String gettable() {
        return "services";
    }
    @GetMapping("/portfolio")
    public String getcomponents() {
        return "portfolio";
    }
    @GetMapping("/typography")
    public String gettypography() {
        return "typography";
    }
    @GetMapping("/icon-variations")
    public String geticon_variations() {
        return "icon-variations";
    }
    @GetMapping("/icons")
    public String geticons() {
        return "icons";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/about")
    public String getabout() {
        return "about";
    }
    @GetMapping("/pricingbox")
    public String getpricingbox() {
        return "pricingbox";
    }
    @GetMapping("/testimonials")
    public String gettestimonials() {
        return "testimonials";
    }
    @GetMapping("/404")
    public String get404() {
        return "404";
    }
    @GetMapping("/portfolio-2cols")
    public String getportfolio_2cols() {
        return "portfolio-2cols";
    }
    @GetMapping("/portfolio-3cols")
    public String getportfolio_3cols() {
        return "portfolio-3cols";
    }
    @GetMapping("/portfolio-4cols")
    public String getportfolio_4cols() {
        return "portfolio-4cols";
    }
    @GetMapping("/portfolio-detail")
    public String getportfolio_detail() {
        return "portfolio-detail";
    }
    @GetMapping("/contact")
    public String getcontact() {
        return "contact";
    }
    @GetMapping("/blog-left-sidebar")
    public String getblog_left_sidebar() {
        return "blog-left-sidebar";
    }
    @GetMapping("/blog-right-sidebar")
    public String getblog_right_sidebar() {
        return "blog-right-sidebar";
    }
    @GetMapping("/post-left-sidebar")
    public String getpost_left_sidebar() {
        return "post-left-sidebar";
    }
    @GetMapping("/post-right-sidebar")
    public String getpost_right_sidebar() {
        return "post-right-sidebar";
    }
   

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }

}
