/**
 * 
 */
package tools.datasync.pump;

import java.util.concurrent.atomic.AtomicBoolean;

import tools.datasync.api.PeerMode;
import tools.datasync.api.conflict.ConflictResolver;
import tools.datasync.api.dao.GenericDao;
import tools.datasync.api.utils.HashGenerator;
import tools.datasync.api.utils.Stringify;
import tools.datasync.dao.SyncPairConfig;
import tools.datasync.dao.jdbc.GenericJdbcDao;
import tools.datasync.seed.SeedConsumer;
import tools.datasync.seed.SeedProducer;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author Upendra Jariya
 * @sponsor Douglas Johnson
 * @version 1.0
 * @since 29-Nov-2014
 */
public class DefaultSyncPumpFactory implements SyncPumpFactory {

    private SyncPair syncPair;
    private SyncPairConfig syncPairConfig;
    private SyncConcurArgs concurArgs;
    private SyncStateInitializer syncStateInitializer;

    private GenericJdbcDao sourceDao = null;
    private GenericJdbcDao targetDao = null;

    private HashGenerator hasher;
    private Stringify stringify;

    public DefaultSyncPumpFactory(SyncPair pair, SyncPairConfig syncPairConfig,
	    SyncStateInitializer syncStateInitializer,
	    SyncConcurArgs concurArgs) {
	this.syncPair = pair;
	this.syncPairConfig = syncPairConfig;
	this.syncStateInitializer = syncStateInitializer;
	this.concurArgs = concurArgs;
    }

    private SyncPumpSender createSender(SyncPeerParms peer,
	    GenericDao sourceDao, SyncStateInitializer syncStateInitializer,
	    SyncConcurArgs concurArgs) {
	SeedProducer seedProducer = syncPairConfig.getSeedProducerFactory()
		.create(syncStateInitializer.getTables(),
			syncStateInitializer.getEntityGetter(), sourceDao);

	SyncPumpSender sender = new SyncPumpSender(peer.getSendQueue(),
		syncStateInitializer, concurArgs);

	sender.setSeedProducer(seedProducer);
	sender.setAckPairReceiverLatch(peer.getAckPairReceiverLatch());
	sender.setAckPeerSenderLatch(peer.getAckPeerSenderLatch());
	return (sender);
    }

    private SyncPumpReceiver createReceiver(SyncPeerParms peer,
	    GenericDao targetDao, ConflictResolver conflictResolver,
	    AtomicBoolean stopper) {
	SyncPumpReceiver receiver = new SyncPumpReceiver(
		peer.getReceiveQueue(), stopper);

	SeedConsumer seedConsumer = syncPairConfig.getSeedConsumerFactory()
		.create(conflictResolver, targetDao);

	receiver.setSeedConsumer(seedConsumer);
	receiver.setAckPairReceiverLatch(peer.getAckPairReceiverLatch());
	receiver.setAckPeerSenderLatch(peer.getAckPeerSenderLatch());
	receiver.setArrayList(peer.getArrayList());
	receiver.setNextEntitySignaler(concurArgs.getNextEntitySignaler());

	return (receiver);
    }

    private SyncPeerParms calculateReceiverPeer(PeerMode peerMode) {
	return syncPair.getPeerMe();
    }

    private SyncPeerParms calculateSenderPeer(PeerMode peerMode) {
	return syncPair.getPeerMe();
    }

    public SyncPump getInstance(PeerMode peerMode)
	    throws InstantiationException {

	SyncPeerParms senderPeer = calculateSenderPeer(peerMode);
	SyncPeerParms receiverPeer = calculateReceiverPeer(peerMode);

	SyncPumpSender sender = createSender(senderPeer,
		syncPairConfig.getSourceDao(), syncStateInitializer, concurArgs);
	SyncPumpReceiver receiver = createReceiver(receiverPeer,
		syncPairConfig.getTargetDao(), syncPairConfig
			.getConflictResolverFactory().create(peerMode),
		concurArgs.getStopper());

	return new DefaultSyncPump(peerMode, sender, receiver);

    }

    public HashGenerator getHasher() {
	return hasher;
    }

    public void setHasher(HashGenerator hasher) {
	this.hasher = hasher;
    }

    public Stringify getStringify() {
	return stringify;
    }

    public void setStringify(Stringify stringify) {
	this.stringify = stringify;
    }

    public GenericDao getSourceDao() {
	return sourceDao;
    }

    public GenericDao getTargetDao() {
	return targetDao;
    }

}
