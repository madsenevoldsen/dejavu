package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.DejaVuEngine;
import com.jayway.dejavu.core.interfaces.EngineFactory;

public class RecordReplayFactory implements EngineFactory {

    @Override
    public DejaVuEngine getEngine() {
        return new RecordReplayer();
    }
}
