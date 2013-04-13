package com.jayway.dejavu.core;

import com.jayway.dejavu.core.impl.ZipFileEnumeration;
import com.jayway.dejavu.core.typeinference.TypeInference;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.zip.ZipEntry;

public class AutoImpureTypeInference implements TypeInference {

    @Override
    public Class<?> inferType(Object instance, MethodSignature signature) {
        if ( signature.getMethod().getDeclaringClass() == ZipFileEnumeration.class ) {
            if ( signature.getName().equals("nextElement") ) {
                return ZipEntry.class;
            }
        }
        return null;
    }
}
