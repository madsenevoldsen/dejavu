package com.jayway.dejavu.core;

import com.jayway.dejavu.core.marshaller.MarshallerPlugin;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

import static org.easymock.EasyMock.createMock;

public class AutoImpureMarshallerPlugin implements MarshallerPlugin  {

    private static final Set<String> autoImpureClasses;

    static {
        Set<String> classes = new HashSet<String>();
        // so far these are the supported classes
        classes.add( ZipEntry.class.getName() );
        classes.add( InputStream.class.getName() );

        autoImpureClasses = Collections.unmodifiableSet( classes );
    }

    @Override
    public Object unmarshal(Class<?> clazz, String marshalValue ) {
        if ( clazz == Class.class && autoImpureClasses.contains( marshalValue )) {
            // create mock instance
            try {
                return createMock( Class.forName(marshalValue) );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not create class: "+marshalValue);
            }
        }
        return null;
    }

    @Override
    public String marshalObject(Object value) {
        if ( autoImpureClasses.contains( value.getClass().getName()) ) {
            // serialize to default value
            return value.getClass().getSimpleName() + ".class";
        }
        return null;
    }
}
