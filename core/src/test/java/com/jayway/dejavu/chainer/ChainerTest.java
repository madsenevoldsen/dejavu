package com.jayway.dejavu.chainer;

import com.jayway.dejavu.core.chainer.ChainBuilder;
import com.jayway.dejavu.core.chainer.CouldNotHandleException;
import junit.framework.Assert;
import org.junit.Test;

public class ChainerTest {

    @Test
    public void testChain() {
        Handle handle = ChainBuilder.handle(Handle.class).add(new StringHandler(), new ExceptionHandle()).build();

        Assert.assertEquals("Exception", handle.name(RuntimeException.class));
        Assert.assertEquals("String class", handle.name(String.class));
    }

    @Test
    public void testError() {
        Handle handle = ChainBuilder.handle(Handle.class).add(new StringHandler(), new ExceptionHandle()).build();

        Assert.assertEquals("Exception", handle.name(RuntimeException.class));
        Assert.assertEquals("String class", handle.name(String.class));

        try {
            handle.name( Integer.class );
            Assert.fail();
        } catch (CouldNotHandleException e) {

        }
    }

    /*@Test
    public void constructError() {
        try {
            ChainBuilder.chain(Handle.class).handle();
            Assert.fail();
        } catch (BuildException e) {

        }
    } */

    @Test
    public void returningNull() {
        Handle handle = ChainBuilder.handle(Handle.class).add(new ReturnNullHandler()).build();

        Assert.assertEquals( "integer type", handle.name( Integer.class ) );
        Assert.assertNull( handle.name( String.class ));
        try {
            handle.name( Boolean.class );
            Assert.fail();
        } catch (CouldNotHandleException e  ) {

        }
    }
}
