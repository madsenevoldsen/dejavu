package com.jayway.dejavu.core;

import com.jayway.dejavu.core.impl.ZipFileEnumeration;
import com.jayway.dejavu.core.typeinference.TypeInference;

import java.util.zip.ZipEntry;

public class AutoImpureTypeInference implements TypeInference {

    @Override
    public Class<?> inferType(Object instance, DejaVuInterception interception ) {
        if ( interception.getMethod().getDeclaringClass() == ZipFileEnumeration.class ) {
            if ( interception.getMethod().getName().equals("nextElement") ) {
                return ZipEntry.class;
            }
        }
        return null;
    }
}
