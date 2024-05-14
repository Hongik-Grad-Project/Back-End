package trackers.demo.login.oauth.dto;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    @Override
    public Map<String, Object> getAttributes() {
        // 리소스 서버별로 반환하는 Attribute 형태가 달라 획일화가 어려워 사용하지 않음 -> 아래에서 따로 구현
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {    // Role 값 리턴

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userDTO.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return userDTO.getUsername();
    }

    public String getAuthKey(){
        return userDTO.getAuthKey();
    }
}
