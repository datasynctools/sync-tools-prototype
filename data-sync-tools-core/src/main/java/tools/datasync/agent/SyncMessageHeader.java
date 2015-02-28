package tools.datasync.agent;

public class SyncMessageHeader {

    private String originId; // peer id
    private long messageNumber; // required for ACK/ NACK
    private String messageType;
    private long timestamp;

    public SyncMessageHeader(String originId, long messageNumber,
	    String messageType, long timestamp) {
	super();
	this.originId = originId;
	this.messageNumber = messageNumber;
	this.messageType = messageType;
	this.timestamp = timestamp;
    }

    public String getOriginId() {
	return originId;
    }

    public long getMessageNumber() {
	return messageNumber;
    }

    public String getMessageType() {
	return messageType;
    }

    public long getTimestamp() {
	return timestamp;
    }

    public void setOriginId(String originId) {
	this.originId = originId;
    }

    public void setMessageNumber(long messageNumber) {
	this.messageNumber = messageNumber;
    }

    public void setMessageType(String messageType) {
	this.messageType = messageType;
    }

    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }

}
