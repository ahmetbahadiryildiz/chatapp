package com.greemlock.edutherapist.Objects;

import java.io.Serializable;

public class ObjectMessage implements Serializable {

    private String message_id;
    private String message_uid;
    private String message;
    private String message_date;

    public ObjectMessage() {
    }

    public ObjectMessage(String message_id,String message_uid ,String message, String message_date) {
        this.message_id = message_id;
        this.message_uid = message_uid;
        this.message = message;
        this.message_date = message_date;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_uid() {
        return message_uid;
    }

    public void setMessage_uid(String message_uid) {
        this.message_uid = message_uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_date() {
        return message_date;
    }

    public void setMessage_date(String message_date) {
        this.message_date = message_date;
    }
}
