//package trackers.demo.loginv1.oauth;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import trackers.demo.loginv1.oauth.dto.*;
//import trackers.demo.user.domain.User;
//import trackers.demo.user.domain.repository.UserRepository;
//import trackers.demo.global.login.oauth.dto.*;
//
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    // 리소스 서버에서 제공하는 사용자 정보를 받기 위한 메서드
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
//
//        /* 리소스 서버에서 사용자 정보를 제공하는 로직 */
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        System.out.println(oAuth2User);
//
//        String registrationID = userRequest.getClientRegistration().getRegistrationId();
//
//        // 네이버면 네이버 DTO로 받고, 구글이면 구글 DTO로 받음 -> OAuth2Response를 구현해서 userRequest 데이터를 받아야함
//        OAuth2Response oAuth2Response = null;
//        if(registrationID.equals("naver")){
//            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
//        } else if (registrationID.equals(("google"))) {
//            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
//        }else {
//            return null;
//        }
//
//        /* 로그인을 진행하는 로직 */
//
//        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디 값을 만듦
//        String authkey = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
//
//        User existData = userRepository.findByAuthkey(authkey);
//        if(existData == null){
//
//            // DB에 사용자 정보 저장
//            User user = User.builder()
//                    .authkey(authkey)
//                    .email(oAuth2Response.getEmail())
//                    .username(oAuth2Response.getUsername())
//                    .role("ROLE_USER")
//                    .build();
//            userRepository.save(user);
//
//            // 최종적으로 OAuth2LoginAuthenticationProvider에 넘겨줄 DTO
//            UserDTO userDTO = UserDTO.builder()
//                    .authKey(authkey)
//                    .username(oAuth2Response.getUsername())
//                    .role("ROLE_USER")
//                    .build();
//
//            return new CustomOAuth2User(userDTO);
//
//        } else {
//
//            existData.setEmail(oAuth2Response.getEmail());
//            existData.setName(oAuth2Response.getUsername());
//
//            userRepository.save(existData);
//
//            UserDTO userDTO = UserDTO.builder()
//                    .authKey(authkey)
//                    .username(oAuth2Response.getUsername())
//                    .role(existData.getRole())
//                    .build();
//
//            return new CustomOAuth2User(userDTO);
//
//        }
//
//    }
//
//}
