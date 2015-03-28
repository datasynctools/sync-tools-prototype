/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.util.concurrent.atomic.AtomicBoolean;

import tools.datasync.api.utils.HashGenerator;
import tools.datasync.api.utils.Stringify;
import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.dao.GenericJdbcDao;
import tools.datasync.basic.dao.SyncPairConfig;
import tools.datasync.basic.logic.ConflictResolver;
import tools.datasync.basic.seed.DbSeedProducer;
import tools.datasync.basic.seed.SeedConsumer;

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
public class JvmSyncPumpFactory implements SyncPumpFactory {

    private JvmSyncPair syncPair;
    private SyncPairConfig syncPairConfig;
    private JvmSyncConcurArgs concurArgs;
    private SyncStateInitializer syncStateInitializer;

    private GenericJdbcDao sourceDao = null;
    private GenericJdbcDao targetDao = null;

    private HashGenerator hasher;
    private Stringify stringify;

    public JvmSyncPumpFactory(JvmSyncPair pair, SyncPairConfig syncPairConfig,
	    SyncStateInitializer syncStateInitializer,
	    JvmSyncConcurArgs concurArgs) {
	this.syncPair = pair;
	this.syncPairConfig = syncPairConfig;
	this.syncStateInitializer = syncStateInitializer;
	this.concurArgs = concurArgs;
    }

    private static JvmSyncPumpSender createSender(JvmSyncPeerParms peer,
	    GenericDao sourceDao, SyncStateInitializer syncStateInitializer,
	    JvmSyncConcurArgs concurArgs) {

	DbSeedProducer seedProducer = new DbSeedProducer(sourceDao,
		syncStateInitializer.getTables(),
		syncStateInitializer.getEntityGetter());

	JvmSyncPumpSender sender = new JvmSyncPumpSender(peer.getSendQueue(),
		syncStateInitializer, concurArgs);

	sender.setSeedProducer(seedProducer);
	sender.setAckPairReceiverLatch(peer.getAckPairReceiverLatch());
	sender.setAckPeerSenderLatch(peer.getAckPeerSenderLatch());
	return (sender);
    }

    private JvmSyncPumpReceiver createReceiver(JvmSyncPeerParms peer,
	    GenericDao targetDao, ConflictResolver conflictResolver,
	    AtomicBoolean stopper) {
	JvmSyncPumpReceiver receiver = new JvmSyncPumpReceiver(
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

    private JvmSyncPeerParms calculateReceiverPeer(PeerMode peerMode) {
	return syncPair.getPeerMe();
    }

    private JvmSyncPeerParms calculateSenderPeer(PeerMode peerMode) {
	return syncPair.getPeerMe();
    }

    public SyncPump getInstance(PeerMode peerMode)
	    throws InstantiationException {

	JvmSyncPeerParms senderPeer = calculateSenderPeer(peerMode);
	JvmSyncPeerParms receiverPeer = calculateReceiverPeer(peerMode);

	JvmSyncPumpSender sender = createSender(senderPeer,
		syncPairConfig.getSourceDao(), syncStateInitializer, concurArgs);
	JvmSyncPumpReceiver receiver = createReceiver(receiverPeer,
		syncPairConfig.getTargetDao(), syncPairConfig
			.getConflictResolverFactory().create(peerMode),
		concurArgs.getStopper());

	return new JvmSyncPump(peerMode, sender, receiver);

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
