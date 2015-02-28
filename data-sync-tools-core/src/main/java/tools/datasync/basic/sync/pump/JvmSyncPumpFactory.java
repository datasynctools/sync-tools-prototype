/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.util.concurrent.atomic.AtomicBoolean;

import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.dao.GenericJDBCDao;
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

    private GenericJDBCDao sourceDao = null;
    private GenericJDBCDao targetDao = null;

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

	JvmSyncPumpSender sender = new JvmSyncPumpSender(peer.getQueue(),
		syncStateInitializer, concurArgs.getStopper());

	sender.setSeedProducer(seedProducer);
	sender.setAckPairReceiverLatch(peer.getAckPairReceiverLatch());
	sender.setAckPeerSenderLatch(peer.getAckPeerSenderLatch());
	sender.setBeginSenderLatch(concurArgs.getBeginSenderLatch());
	return (sender);
    }

    private JvmSyncPumpReceiver createReceiver(JvmSyncPeerParms peer,
	    GenericDao targetDao, ConflictResolver conflictResolver,
	    AtomicBoolean stopper) {
	JvmSyncPumpReceiver receiver = new JvmSyncPumpReceiver(peer.getQueue(),
		stopper);

	SeedConsumer seedConsumer = syncPairConfig.getSeedConsumerFactory()
		.create(conflictResolver, targetDao);

	receiver.setSeedConsumer(seedConsumer);
	receiver.setAckPairReceiverLatch(peer.getAckPairReceiverLatch());
	receiver.setAckPeerSenderLatch(peer.getAckPeerSenderLatch());

	return (receiver);
    }

    public SyncPump getInstance(PeerMode peerMode)
	    throws InstantiationException {

	JvmSyncPumpSender sender = createSender(syncPair.getPeerMe(),
		syncPairConfig.getSourceDao(), syncStateInitializer, concurArgs);
	JvmSyncPumpReceiver receiver = createReceiver(syncPair.getPeerMe(),
		syncPairConfig.getTargetDao(), syncPairConfig
			.getConflictResolverFactory().create(peerMode),
		concurArgs.getStopper());
	return new JvmSyncPump(peerMode, sender, receiver);

    }

    public GenericDao getSourceDao() {
	return sourceDao;
    }

    public GenericDao getTargetDao() {
	return targetDao;
    }

}
