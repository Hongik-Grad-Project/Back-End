package trackers.demo.login.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO {

    private String role;
    private String username;
    private String authKey;
}
