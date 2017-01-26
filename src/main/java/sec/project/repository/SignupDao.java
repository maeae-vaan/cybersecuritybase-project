package sec.project.repository;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sec.project.domain.Signup;

@Repository
public class SignupDao extends JdbcTemplate {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Override
    @Autowired
    public void setDataSource(final DataSource ds) {
        super.setDataSource(ds);
        logger.info("setDataSource: {}", ds);
    }
    
    public long save(final Signup sup) {
        final long id = super.queryForObject("SELECT IFNULL(MAX(ID),0)+1 FROM signup", Long.class);
        String sql = "INSERT INTO signup(id,username, name,address) VALUES (" +
                id + "," +
                "'" + sup.getUsername() + "'," +
                "'" + sup.getName() + "'," +
                "'" + sup.getAddress() + "')";
        logger.info("SQL: {}", sql);
        super.execute(sql);
        return id;
    }
}
