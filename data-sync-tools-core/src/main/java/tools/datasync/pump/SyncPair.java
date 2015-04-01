package tools.datasync.pump;

public class SyncPair {

    private SyncPeerParms peerMe;
    private SyncPeerParms peerOther;

    public SyncPair(SyncPeerParms peerMe, SyncPeerParms peerOther) {
	this.peerMe = peerMe;
	this.peerOther = peerOther;
    }

    public SyncPeerParms getPeerMe() {
	return peerMe;
    }

    public SyncPeerParms getPeerOther() {
	return peerOther;
    }

}