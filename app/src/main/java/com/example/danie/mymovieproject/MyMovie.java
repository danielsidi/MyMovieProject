package com.example.danie.mymovieproject;

/**
 * Created by danie on 08/02/2017.
 */

public class MyMovie {

    private int _id;
    private String subject;
    private String body;
    private String url;
    private String rating;

    public MyMovie( String subject, String body, String url) {

        this.subject = subject;
        this.body = body;
        this.url = url;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return subject;
    }
}
