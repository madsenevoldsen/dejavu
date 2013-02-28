package com.jayway.dejavu.core.marshaller;

import java.util.List;

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
    public String asTraceBuilderArgument(Object value) {
        if ( value == null ) return null;
        String string = current.asTraceBuilderArgument( value );
        if ( string != null ) return string;
        if ( next == null ) return null;

        return next.asTraceBuilderArgument( value );
    }

    protected List<Class> getClasses( List<Class> classes ) {
        if ( !(current instanceof SimpleTypeMarshaller) ) {
            classes.add( current.getClass() );
        }
        if ( next != null ) next.getClasses( classes );
        return classes;
    }
}