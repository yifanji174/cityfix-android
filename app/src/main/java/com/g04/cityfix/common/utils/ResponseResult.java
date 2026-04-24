package com.g04.cityfix.common.utils;

/**
 * Callback result in use of Firestore
 * @author Jerry Yang
 */

public class ResponseResult {
    private boolean success;
    private Exception e;

    public ResponseResult(boolean success, Exception e) {
        this.success = success;
        this.e = e;
    }

    public ResponseResult() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}
