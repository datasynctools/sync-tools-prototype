package tools.datasync.basic.sync.pump;

public interface NextEntitySignaler {

    void tellPeerReadyForNextEntity(String previousEntityId);

}
