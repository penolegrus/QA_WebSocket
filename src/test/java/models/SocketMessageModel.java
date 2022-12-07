package models;

public class SocketMessageModel {
    private String type;
    private String topic;
    private String subject;
    private TickerDataModel data;

    public SocketMessageModel() {
    }

    public SocketMessageModel(String type, String topic, String subject, TickerDataModel data) {
        this.type = type;
        this.topic = topic;
        this.subject = subject;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public TickerDataModel getData() {
        return data;
    }

    public void setData(TickerDataModel data) {
        this.data = data;
    }
}
