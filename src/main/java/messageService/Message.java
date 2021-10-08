package messageService;

public class Message {

    protected String senderName;
    protected String message;

    public Message(String senderName, String message) {
        this.senderName = senderName;
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "From: " + senderName +
                " Message: " + message;
    }
}
