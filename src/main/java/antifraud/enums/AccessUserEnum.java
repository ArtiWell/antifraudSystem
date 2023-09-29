package antifraud.enums;

import lombok.Getter;

@Getter
public enum AccessUserEnum {
    LOCK("locked", false),
    UNLOCK("unlocked", true);

    final String responseStatus;
    final boolean isLocked;

    AccessUserEnum(String s, boolean b) {
        responseStatus = s;
        isLocked = b;
    }
}
