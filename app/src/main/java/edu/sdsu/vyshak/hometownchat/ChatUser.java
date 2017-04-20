package edu.sdsu.vyshak.hometownchat;

/**
 * Created by vysha on 4/18/2017.
 */

public class ChatUser {
    private String nickname;
    private String email;
    protected String uid;
    public void ChatUser(){

    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}


