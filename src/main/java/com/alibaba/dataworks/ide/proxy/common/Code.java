package com.alibaba.dataworks.ide.proxy.common;

import java.io.Serializable;

public enum Code implements Serializable {
    BASE_SUCCESS(0),
    SUCCESS(200),
    ParamsError(403),
    ParamsNull(120),
    Exception(206),
    ERROR(500),
    BASE_ERROR(-1);

    public int code;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private Code(int code) {
        this.code = code;
    }
}
