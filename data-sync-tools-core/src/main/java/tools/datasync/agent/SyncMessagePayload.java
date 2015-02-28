package tools.datasync.agent;

public class SyncMessagePayload {

    private String payloadData;
    private String payloadHash;

    public SyncMessagePayload(String payloadData, String payloadHash) {
	super();
	this.payloadData = payloadData;
	this.payloadHash = payloadHash;
    }

    public String getPayloadJson() {
	return payloadData;
    }

    public String getPayloadHash() {
	return payloadHash;
    }

    public void setPayloadData(String payloadData) {
	this.payloadData = payloadData;
    }

    public void setPayloadHash(String payloadHash) {
	this.payloadHash = payloadHash;
    }

}
