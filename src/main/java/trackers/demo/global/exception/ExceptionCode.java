package trackers.demo.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),

    // 멤버 에러
    NOT_FOUND_MEMBER(1010, "요청한 ID에 해당하는 멤버가 존재하지 않습니다."),
    FAIL_TO_CREATE_NEW_MEMBER(1012, "새로운 멤버를 생성하는데 실패하였습니다."),
    NOT_FOUND_RECOMMEND_PROJECT_STRATEGY(1015, "요청에 해당하는 프로젝트 추천 전략이 존재하지 않습니다."),

    // 프로젝트  에러
    NOT_FOUND_PROJECT(2001, "요청한 ID에 해당하는 프로젝트가 존재하지 않습니다"),
    NOT_FOUND_TARGET(2002, "요청한 프로젝트 대상에 해당하는 대상이 존재하지 않습니다"),
    NOT_FOUND_TAG(2003, "요청한 프로젝트 태그에 해당하는 태그가 존재하지 않습니다"),
    INVALID_NOT_COMPLETED_PROJECT_WITH_MEMBER(2004, "요청한 임시 저장된 프로젝트가 존재하지 않습니다"),
    INVALID_PROJECT_WITH_MEMBER(2005, "요청한 프로젝트가 존재하지 않습니다"),
    UNSUPPORTED_SORT_PARAMETER(2006, "지원하지 않는 정렬 방식입니다."),

    // 오로라 AI 에러
    NOT_FOUND_CHAT_ROOM(3001, "요청한 ID에 해당하는 채팅방이 존재하지 않습니다"),
    NOT_FOUND_ASSISTANT(3002, "요청 이름에 해당하는 Assistant가 존재하지 않습니다."),
    NOT_FOUND_MESSAGE_IN_THREAD(3003, "Thread에 메시지가 존재하지 않습니다"),

    // 이미지 에러
    EXCEED_IMAGE_CAPACITY(5001, "업로드 가능한 이미지 용량을 초과했습니다."),
    NULL_IMAGE(5002, "업로드한 이미지 파일이 NULL입니다."),
    EMPTY_IMAGE(5003, "최소 한 장 이상의 이미지를 업로드해야합니다."),
    EXCEED_IMAGE_LIST_SIZE(5004, "업로드 가능한 이미지 개수를 초과했습니다."),
    INVALID_IMAGE_URL(5005, "요청한 이미지 URL의 형식이 잘못되었습니다."),
    INVALID_IMAGE_PATH(5101, "이미지를 저장할 경로가 올바르지 않습니다."),
    FAIL_IMAGE_NAME_HASH(5102, "이미지 이름을 해싱하는 데 실패했습니다."),
    INVALID_IMAGE(5103, "올바르지 않은 이미지 파일입니다."),

    // 관리자 에러
    INVALID_USER_NAME(8001, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(8002, "비밀번호가 일치하지 않습니다."),
    NULL_ADMIN_AUTHORITY(8101, "잘못된 관리자 권한입니다."),
    DUPLICATED_ADMIN_USERNAME(8102, "중복된 사용자 이름입니다."),
    NOT_FOUND_ADMIN_ID(8103, "요청한 ID에 해당하는 관리자를 찾을 수 없습니다."),
    INVALID_CURRENT_PASSWORD(8104, "현재 사용중인 비밀번호가 일치하지 않습니다."),
    INVALID_ADMIN_AUTHORITY(8201, "해당 관리자 기능에 대한 접근 권한이 없습니다."),

    // 인증 에러
    INVALID_AUTHORIZATION_CODE(9001, "유효하지 않은 인증 코드입니다."),
    NOT_SUPPORTED_OAUTH_SERVICE(9002, "해당 OAuth 서비스는 제공하지 않습니다."),
    FAIL_TO_CONVERT_URL_PARAMETER(9003, "Url Parameter 변환 중 오류가 발생했습니다."),
    INVALID_REFRESH_TOKEN(9101, "올바르지 않은 형식의 RefreshToken입니다."),
    INVALID_ACCESS_TOKEN(9102, "올바르지 않은 형식의 AccessToken입니다."),
    EXPIRED_PERIOD_REFRESH_TOKEN(9103, "기한이 만료된 RefreshToken입니다."),
    EXPIRED_PERIOD_ACCESS_TOKEN(9104, "기한이 만료된 AccessToken입니다."),
    FAIL_TO_VALIDATE_TOKEN(9105, "토큰 유효성 검사 중 오류가 발생했습니다."),
    NOT_FOUND_REFRESH_TOKEN(9106, "refresh-token에 해당하는 쿠키 정보가 없습니다."),
    INVALID_AUTHORITY(9201, "해당 요청에 대한 접근 권한이 없습니다."),

    INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다. 관리자에게 문의해 주세요.");

    private final int code;

    private final String message;
}
