package sec.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupDao;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private SignupDao signupDao;

    @RequestMapping("*")
    public String defaultMapping(final Authentication a) {
        final boolean isAdmin = a.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
        if (isAdmin) {
            return "redirect:/signups";
        }
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(
            final Authentication a,
            final ModelMap model,
            final @RequestParam String name,
            final @RequestParam String address) {
        long id = signupDao.save(new Signup(a.getName(), name, address));
        model.addAttribute("address", address);
        model.addAttribute("signupId", id);

        logger.info("Current signup count: {}", signupRepository.count());
        return "done";
    }

    @RequestMapping(value = "/view/{signupId}")
    public String viewSignup(final @PathVariable Long signupId, final ModelMap model) {
        final Signup su = signupRepository.findOne(signupId);
        model.addAttribute("name", su.getName());
        model.addAttribute("address", su.getAddress());
        return "view";
    }

}
