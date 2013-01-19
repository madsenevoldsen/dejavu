package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.IntegrationPoint;
import com.jayway.dejavu.core.annotation.Traced;

public class SickProviderUseCase {

    @Traced
    public void run() {
        sick();
    }

    @IntegrationPoint
    private void sick() {
        throw new MyOwnException();
    }
}
