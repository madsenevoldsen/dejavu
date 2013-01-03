package com.jayway.dejavu;

import com.jayway.dejavu.repository.CircuitBreakerRepository;

import java.util.Date;

public class CircuitBreakerRepo implements CircuitBreakerRepository {
    private static Date date;
    private static Exception exception;
    private static Boolean blackout;

    @Override
    public String getName() {
        return "Sick";
    }

    @Override
    public Integer getBlackOutSeconds() {
        return 10;
    }

    @Override
    public Date getExceptionTime() {
        return date;
    }

    @Override
    public void setExceptionTime(Date date) {
        CircuitBreakerRepo.date = date;
    }

    @Override
    public Exception getCurrentException() {
        return exception;
    }

    @Override
    public void setCurrentException(Exception exception) {
        CircuitBreakerRepo.exception = exception;
    }

    @Override
    public Boolean isBlackout() {
        if ( blackout == null ) return false;
        return blackout;
    }

    @Override
    public void setBlackout(Boolean blackout) {
        CircuitBreakerRepo.blackout = blackout;
    }
}
