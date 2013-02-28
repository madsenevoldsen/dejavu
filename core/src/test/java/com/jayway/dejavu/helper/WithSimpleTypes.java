package com.jayway.dejavu.helper;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;

import java.util.Random;

public class WithSimpleTypes {

    @Traced
    public int simple() {
        int result = 1;
        int range = random();
        for ( int i=0; i<range; i++) {
            result *= random();
        }
        return result;
    }

    @Impure
    private Integer random() {
        return new Random().nextInt( 10 )+1;
    }

}
