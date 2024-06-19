package trackers.demo.admin.domain.type;

import trackers.demo.global.exception.AdminException;
import trackers.demo.global.exception.ExceptionCode;

import java.util.Arrays;

public enum AdminType {

    ADMIN,

    MASTER;

    public static AdminType getMappedAdminType(final String adminType){
        return Arrays.stream(values())
                .filter(value -> value.name().toUpperCase().equals(adminType))
                .findAny()
                .orElseThrow(() -> new AdminException(ExceptionCode.NULL_ADMIN_AUTHORITY));
    }
}
