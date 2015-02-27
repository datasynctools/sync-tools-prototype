package tools.datasync.basic.sync.pump;

public class JvmSyncPair {

    private JvmSyncPeerParms peerMe;
    private JvmSyncPeerParms peerOther;

    public JvmSyncPair(JvmSyncPeerParms peerMe, JvmSyncPeerParms peerOther) {
	this.peerMe = peerMe;
	this.peerOther = peerOther;
    }

    public JvmSyncPeerParms getPeerMe() {
	return peerMe;
    }

    public JvmSyncPeerParms getPeerOther() {
	return peerOther;
    }

}