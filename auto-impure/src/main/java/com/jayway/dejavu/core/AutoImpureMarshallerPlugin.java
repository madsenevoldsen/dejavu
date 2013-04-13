package com.jayway.dejavu.core;

import com.jayway.dejavu.core.marshaller.MarshallerPlugin;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

import static org.easymock.EasyMock.createMock;

public class AutoImpureMarshallerPlugin implements MarshallerPlugin  {

    private static final Set<Class<?>> autoImpureClasses;

    static {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        // so far these are the supported classes
        classes.add( ZipEntry.class );
        classes.add( InputStream.class );

        autoImpureClasses = Collections.unmodifiableSet( classes );
    }

    @Override
    public Object unmarshal(Class<?> clazz, String marshalValue ) {
        if ( autoImpureClasses.contains( clazz )) {
            // create mock instance
            return createMock( clazz );
        }
        return null;
    }

    @Override
    public String marshalObject(Object value) {
        if ( autoImpureClasses.contains( value.getClass()) ) {
            // serialize to default value
            return "";
        }
        return null;
    }

    @Override
    public String asTraceBuilderArgument(TraceElement element ) {
        if ( element.getType() == null ) {
            return element.getValue().getClass().getSimpleName() + ".class";
        } else {
            return element.getType().getSimpleName() + ".class";
        }
    }
}
