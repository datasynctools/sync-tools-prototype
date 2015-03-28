package tools.datasync.pump;

public class SenderPreAckLogicResult {

    private boolean continueProcessing;
    private long messageCount;

    public SenderPreAckLogicResult(boolean continueProcessing, long messageCount) {
	super();
	this.continueProcessing = continueProcessing;
	this.messageCount = messageCount;
    }

    public boolean isContinueProcessing() {
	return continueProcessing;
    }

    public long getMessageCount() {
	return messageCount;
    }

}
