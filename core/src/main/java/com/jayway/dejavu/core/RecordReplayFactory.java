package com.jayway.dejavu.core;

public class RecordReplayFactory implements PolicyFactory {

    @Override
    public DejaVuPolicy getPolicy() {
        return new RecordReplayer();
    }
}
