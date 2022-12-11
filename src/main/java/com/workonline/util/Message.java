package com.workonline.util;


import java.io.Serializable;

public class Message implements Serializable {
    /* command c->s
    register #username #pwd
    login #username #pwd
    create_room $document
    enter #roomid
    quit_room #roomid
    close_room #roomid
    operation #roomid $operation

    s->c
    register_success
    register_fail_id_used

    login_success
    login_fail

    create_room_success #roomid

    enter_room_success #roomid
    enter_room_fail

    room_closed #roomid

    broadcast #roomid $operation
    send_document #version #roomid $document
    */
    public String command;
    public String user_id; //客户端给服务端发送需要，服务端给客户端不需要发送这个东西
    public Text_Operation operation;
    public String document;

}