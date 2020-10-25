package Model;

public class Message {
    public String receiver;
    public String sender;
    public String text;

    public Message(String receiver, String sender, String text) {
        this.receiver = receiver;
        this.sender = sender;
        this.text = text;
    }

    public Message() {
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
