package com.jayway.dejavu.recordreplay;

import com.jayway.dejavu.core.DejaVuPolicy;
import com.jayway.dejavu.core.PolicyFactory;

public class RecordReplayFactory implements PolicyFactory {

    @Override
    public DejaVuPolicy getPolicy() {
        return new RecordReplayer();
    }
}
