package com.rkey.returnkeyapp.web.rest.errors;

public class InvalidFlowException extends BadRequestAlertException {

    public InvalidFlowException(String message, String entity) {
        super(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, message, entity, "invalidflow");
    }
}
