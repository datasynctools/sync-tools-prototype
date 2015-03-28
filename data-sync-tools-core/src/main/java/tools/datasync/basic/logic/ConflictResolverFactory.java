package tools.datasync.basic.logic;

import tools.datasync.pump.PeerMode;

public interface ConflictResolverFactory {
    ConflictResolver create(PeerMode peerMode);
}
