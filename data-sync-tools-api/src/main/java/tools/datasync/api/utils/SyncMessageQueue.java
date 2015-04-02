package tools.datasync.api.utils;

import java.util.concurrent.TimeUnit;

import tools.datasync.api.msg.SyncMessage;

public interface SyncMessageQueue {

    void put(SyncMessage syncMessage);

    SyncMessage poll(long timeout, TimeUnit unit);

}
