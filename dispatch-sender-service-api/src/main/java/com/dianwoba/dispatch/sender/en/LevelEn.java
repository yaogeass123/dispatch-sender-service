package com.dianwoba.dispatch.sender.en;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */

@Getter
@ToString
public enum LevelEn {

    /**
     * 低优先级
     */
    LOW(1, "低"),

    /**
     * 中优先级
     */
    MEDIUM(2, "中"),

    /**
     * 高优先级
     */
    HIGH(3, "高");



    private final Byte levelCode;

    private final String levelMsg;

    LevelEn(int levelCode, String levelMsg) {
        this.levelCode = (byte) levelCode;
        this.levelMsg = levelMsg;
    }

    public static LevelEn get(byte levelCode) {
        for (LevelEn en : LevelEn.values()) {
            if (en.getLevelCode() == levelCode) {
                return en;
            }
        }
        return LOW;
    }
}
