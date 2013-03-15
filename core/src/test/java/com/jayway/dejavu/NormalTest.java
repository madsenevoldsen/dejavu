package com.jayway.dejavu;

import org.junit.Test;

import java.util.Random;

/**
 * A test that illustrates that the printed stack trace cannot tell us
 * much about the execution path between the stack elements. Line 26 and
 * 17 are the two elements, but we cannot decide how many times 'c' has
 * been invoked
 */
public class NormalTest {

    @Test
    public void normal(){
        a();
    }

    private void a() {
        int i = new Random().nextInt(10);
        while ( i>0) {
            c();
            i--;
        }
        new Exception().printStackTrace();
    }

    private void c() {
        System.out.println("at c");
    }
}
