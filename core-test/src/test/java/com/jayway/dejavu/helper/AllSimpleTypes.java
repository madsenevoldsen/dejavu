package com.jayway.dejavu.helper;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;

public class AllSimpleTypes {

    @Traced
    public String types() {
        StringBuilder sb = new StringBuilder();
        sb.append( string() ).append( aFloat()).append( aBoolean() ).append( aDouble());
        sb.append( aLong() ).append( anInt() );

        return sb.toString();
    }


    @Impure
    private String string() {
        return "A String";
    }

    @Impure
    private Float aFloat() {
        return 3.4F;
    }

    @Impure
    private Double aDouble() {
        return 4.3;
    }


    @Impure
    private Long aLong() {
        return 41L;
    }

    @Impure
    private Integer anInt(){
        return 14;
    }


    @Impure
    private Boolean aBoolean(){
        return false;
    }
}
