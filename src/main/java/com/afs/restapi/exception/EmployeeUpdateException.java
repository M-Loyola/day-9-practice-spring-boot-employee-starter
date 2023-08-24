package com.afs.restapi.exception;

public class EmployeeUpdateException extends RuntimeException {
    public EmployeeUpdateException() {
        super("Employee is inactive");
    }
}