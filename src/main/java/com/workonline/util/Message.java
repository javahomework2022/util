/**
 * 这个包中包含前后端均需要使用的内容.
 * @author
 * @version JDK17
 */
package com.workonline.util;


import java.io.Serial;
import java.io.Serializable;

/**
 * 本类为前后端通信内容.
 * 传输的命令包括：
 * command c->s:
 * register #username #pwd;
 * login #username #pwd;
 * create_room $document;
 * enter #roomid;
 * quit_room #roomid;
 * close_room #roomid;
 * log_out #username;
 * operation #roomid $operation;
 * s->c:
 * register_success;
 * register_fail_id_used;
 * login_success;
 * login_fail;
 * create_room_success #roomid;
 * enter_room_success #version #roomid $document;
 * enter_room_fail;
 * room_closed #roomid;
 * broadcast #roomid $operation;
 */
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 4720672870300182992L;
    /**
     * 用户命令.
     */
    public String command;
    /**
     * 用户操作.
     */
    public Text_Operation operation;
    /**
     * 需要传输的文本内容.
     */
    public String document;

}