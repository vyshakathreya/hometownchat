package edu.sdsu.vyshak.hometownchat;

import java.util.Date;

/**
 * Created by vysha on 3/27/2017.
 */

public class ChatMessage {

    private String messageText;
    private String reciever;
    private String sender;
    private String senderName;
    private String recieverName;
    private String senderid;

    public ChatMessage(String senderid, String sender, String reciever, String messageText , String messageUserName) {
        this.senderid=senderid;
        this.messageText = messageText; //senderid,sender, recieverid, input.getText().toString(),recieverName
        this.reciever = reciever; //messageUser is reciever
        this.sender= sender;
        this.recieverName = messageUserName;
        // Initialize to current time
        }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public ChatMessage(){

    }

    public String getSenderName() {
        return senderName;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
