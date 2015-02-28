package tools.datasync.agent;

public class SyncMessagePayload {

    private String payloadJson;
    private String payloadHash;

    public SyncMessagePayload(String payloadJson, String payloadHash) {
	super();
	this.payloadJson = payloadJson;
	this.payloadHash = payloadHash;
    }

    public String getPayloadJson() {
	return payloadJson;
    }

    public String getPayloadHash() {
	return payloadHash;
    }

}
