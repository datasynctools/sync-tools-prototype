package tools.datasync.api.conflict;

import tools.datasync.api.PeerMode;

public interface ConflictResolverFactory {
    ConflictResolver create(PeerMode peerMode);
}
