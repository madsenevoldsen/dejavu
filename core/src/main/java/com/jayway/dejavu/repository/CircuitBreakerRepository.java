package com.jayway.dejavu.repository;

import java.util.Date;

public interface CircuitBreakerRepository {
    String getName();
    Integer getBlackOutSeconds();
    Date getExceptionTime();
    void setExceptionTime(Date date);

    Exception getCurrentException();
    void setCurrentException(Exception e);

    Boolean isBlackout();
    void setBlackout(Boolean blackout);
}
