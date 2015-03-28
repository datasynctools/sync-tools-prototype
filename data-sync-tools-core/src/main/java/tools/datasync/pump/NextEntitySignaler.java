package tools.datasync.pump;

public interface NextEntitySignaler {

    void tellPeerReadyForNextEntity(String previousEntityId);

}
