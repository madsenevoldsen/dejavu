package com.jayway.dejavu.core.marshaller;

class MarshallerChain implements MarshallerPlugin {

    static MarshallerChain build( MarshallerPlugin... plugins ) {
        MarshallerChain first = null;
        MarshallerChain current = null;
        if ( plugins != null && plugins.length > 0 ) {
            for (MarshallerPlugin plugin : plugins) {
                MarshallerChain chain = new MarshallerChain(plugin);
                if ( first == null ) {
                    first = chain;
                    current = chain;
                } else {
                    current.next = chain;
                    current = chain;
                }
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
    public Object unmarshal(Class<?> clazz, String jsonValue) {
        Object obj = current.unmarshal(clazz, jsonValue);
        if ( obj != null ) return obj;
        if ( next == null ) return null;

        return next.unmarshal( clazz, jsonValue );
    }

    @Override
    public String marshalObject(Object value) {
        if ( value == null ) return null;
        String string = current.marshalObject( value );
        if ( string != null ) return string;
        if ( next == null ) return null;

        return next.marshalObject( value );
    }
}
