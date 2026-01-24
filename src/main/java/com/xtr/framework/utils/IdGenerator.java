package com.xtr.framework.utils;

import java.util.Random;

public class IdGenerator {

    public synchronized String nextRandomId(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int s = random.nextInt(10);
            val += String.valueOf(s);
        }
        return val;
    }
}
