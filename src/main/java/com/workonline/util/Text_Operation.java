/**
 *这个包中包含前后端均需要使用的内容.
 * @author 不要爆零小组
 * @version JDK17
 *
 */
package com.workonline.util;

import java.io.Serializable;

/**
 * 该类为前后端消息传输时的内容之一，将用户文档版本、用户名与操作结合.
 */
public class Text_Operation implements Serializable {
    /**
     * 用户当前房间当前文档的版本号.
     */
    public int version;
    /**
     * 用户ID.
     */
    public String username;
    /**
     * 用户操作.
     */
    public Operation operation;
    /**
     * 构造器.
     * @param version 用户文档版本号
     * @param username 用户名
     * @param operation 操作
     */
    public Text_Operation(int version, String username, Operation operation) {
        this.version = version;
        this.username = username;
        this.operation = operation;
    }
}
