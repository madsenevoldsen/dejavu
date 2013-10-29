package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;

public class SickProviderUseCase {

    @Traced
    public void run() {
        sick();
    }

    @Impure
    private void sick() {
        throw new MyOwnException();
    }
}
