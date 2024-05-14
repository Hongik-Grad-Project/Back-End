package trackers.demo.auth.domain;

import lombok.Getter;

@Getter
public class Accessor {

    private final Long memberId;

    private final Authority authority;

    private Accessor(final Long userId, final Authority authority){
        this.memberId = userId;
        this.authority = authority;
    }

    public static Accessor user(final Long userId){
        return new Accessor(userId, Authority.MEMBER);
    }

    public static Accessor admin(final Long userId){
        return new Accessor(userId, Authority.ADMIN);
    }

    public boolean isUser(){
        return Authority.MEMBER.equals(authority);
    }

    public boolean isAdmin(){
        return Authority.ADMIN.equals(authority);
    }

}
