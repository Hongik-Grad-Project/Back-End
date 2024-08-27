package trackers.demo.admin.infrastructure;

public interface PasswordEncoder {

    String encode(String Password);

    boolean matches(String password, String hashed);
}
