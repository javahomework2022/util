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
    log_out #username
    operation #roomid $operation

    s->c
    register_success
    register_fail_id_used

    login_success
    login_fail

    create_room_success #roomid

    enter_room_success #version #roomid $document
    enter_room_fail

    room_closed #roomid

    broadcast #roomid $operation
    */
    public String command;
    public Text_Operation operation;
    public String document;

}