package tools.datasync.basic.sync.pump.handlers;

import tools.datasync.api.msg.SyncMessage;

public interface SyncMessageProcessor {

    boolean handle(SyncMessage syncMessage);

}
