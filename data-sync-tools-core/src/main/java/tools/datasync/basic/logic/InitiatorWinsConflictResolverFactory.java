package tools.datasync.basic.logic;

import tools.datasync.basic.sync.pump.PeerMode;

public class InitiatorWinsConflictResolverFactory implements
	ConflictResolverFactory {

    public ConflictResolver create(PeerMode peerMode) {
	if (peerMode.equals(PeerMode.A2B)) {
	    return (new InitiatorWinsConflictResolver(true));
	} else {
	    return (new InitiatorWinsConflictResolver(false));
	}
    }

}
