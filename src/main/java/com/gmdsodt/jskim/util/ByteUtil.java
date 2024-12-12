package com.gmdsodt.jskim.util;

public class ByteUtil {
    public static boolean detectsNonZeroesFrom(byte[] bytes) {
        for (byte b: bytes)
            if (b != 0)
                return true;

        return false;
    };

    public static boolean onlyZeroesIn(byte[] bytes) {
        for (byte b: bytes)
            if (b != 0)
                return false;

        return true;
    };
}
