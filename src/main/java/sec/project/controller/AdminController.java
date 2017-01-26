package sec.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import sec.project.repository.SignupRepository;

@Controller
public class AdminController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping(value = "/signups", method = RequestMethod.GET)
    public ModelAndView loadForm(final Authentication a) {
        final ModelAndView mav = new ModelAndView("list");
        mav.addObject("isAdmin", a.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
        mav.addObject("signups", signupRepository.findAll());
        mav.addObject("user", a);
        return mav;
    }
}
