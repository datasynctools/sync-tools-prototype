package tools.datasync.model;

import tools.datasync.api.PeerMode;
import tools.datasync.api.conflict.ConflictResolver;
import tools.datasync.api.conflict.ConflictResolverFactory;

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
