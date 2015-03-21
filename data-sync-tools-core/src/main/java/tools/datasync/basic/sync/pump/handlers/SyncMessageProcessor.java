package tools.datasync.basic.sync.pump.handlers;

import tools.datasync.basic.comm.SyncMessage;

public interface SyncMessageProcessor {

    boolean handle(SyncMessage syncMessage);

}
