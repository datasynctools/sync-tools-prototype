package tools.datasync.basic.logic;

import tools.datasync.basic.sync.pump.PeerMode;

public interface ConflictResolverFactory {
    ConflictResolver create(PeerMode peerMode);
}
