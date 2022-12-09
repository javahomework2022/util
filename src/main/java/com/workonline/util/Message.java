package com.workonline.util;

/**
 * @className: Message
 * @description: TODO
 * @date: 2022/12/9 21:49
 */
public class Message {
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

    enter_room_success
    enter_room_fail

    room_closed #roomid

    broadcast #roomid $operation
    send_document #version #roomid $document
     */
    public String command;
    public String user_id;
    public Text_Operation operation;
    public String document;

}