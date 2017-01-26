package sec.project.config;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static class UserInfo {
        public String password;
        public String role;

        public UserInfo(final String password, final String role) {
            this.password = password;
            this.role = role;
        }
    };

    private Map<String, UserInfo> accountDetails;

    @PostConstruct
    public void init() {
        // this data would typically be retrieved from a database
        this.accountDetails = new TreeMap<>();
        this.accountDetails.put("bill", new UserInfo("1234", "USER"));
        this.accountDetails.put("ted", new UserInfo("mom", "USER"));
        this.accountDetails.put("rufus", new UserInfo("admin", "ADMIN"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!this.accountDetails.containsKey(username)) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        final UserInfo ui = this.accountDetails.get(username);

        return new org.springframework.security.core.userdetails.User(
                username,
                ui.password,
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(ui.role)));
    }
}
