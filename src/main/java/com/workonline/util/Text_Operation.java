package com.workonline.util;

import java.io.Serializable;

public class Text_Operation implements Serializable {
    public int version;
    public String username;
    public Operation operation;

    public Text_Operation(int version, String username, Operation operation) {
        this.version = version;
        this.username = username;
        this.operation = operation;
    }
}
