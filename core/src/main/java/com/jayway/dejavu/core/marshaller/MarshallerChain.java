package com.jayway.dejavu.core.marshaller;

import com.jayway.dejavu.core.TraceElement;

import java.util.List;

/**
 * Chain of marshallers used inside the Marshaller class.
 * By default it is capable of marshalling exceptions and
 * simple types: Integer, Long, Float, Double, String, and
 * Boolean.
 */
class MarshallerChain implements MarshallerPlugin {

    static MarshallerChain build( MarshallerPlugin... plugins ) {
        MarshallerChain first = new MarshallerChain( new SimpleTypeMarshaller());
        MarshallerChain current = first;
        if ( plugins != null && plugins.length > 0 ) {
            for (MarshallerPlugin plugin : plugins) {
                MarshallerChain chain = new MarshallerChain(plugin);
                current.next = chain;
                current = chain;
            }
        }
        current.next = new MarshallerChain( new JacksonMarshallerPlugin());
        return first;
    }

    private MarshallerChain(MarshallerPlugin current) {
        this.current = current;
    }
    private MarshallerPlugin current;
    private MarshallerChain next;

    @Override
    public Object unmarshal(Class<?> clazz, String marshaled) {
        Object obj = current.unmarshal(clazz, marshaled);
        if ( obj != null ) return obj;
        if ( next == null ) return null;

        return next.unmarshal( clazz, marshaled );
    }

    @Override
    public String marshalObject(Object value) {
        if ( value == null ) return null;
        String string = current.marshalObject( value );
        if ( string != null ) return string;
        if ( next == null ) return null;

        return next.marshalObject( value );
    }

    @Override
    public String asTraceBuilderArgument( TraceElement element ) {
        if ( element.getValue() == null ) return null;
        String string = current.asTraceBuilderArgument( element );
        if ( string != null ) return string;
        if ( next == null ) return null;

        return next.asTraceBuilderArgument( element );
    }

    protected List<Class> getClasses( List<Class> classes ) {
        if ( !(current instanceof SimpleTypeMarshaller)
                && !(current instanceof JacksonMarshallerPlugin)) {
            classes.add( current.getClass() );
        }
        if ( next != null ) next.getClasses( classes );
        return classes;
    }
}