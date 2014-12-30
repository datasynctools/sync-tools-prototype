/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.dao.JDBCSyncStateInitializer;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.seed.SeedException;
import tools.datasync.basic.seed.SeedOverException;
import tools.datasync.basic.seed.SeedProducer;
import tools.datasync.basic.util.HashGenerator;
import tools.datasync.basic.util.JSONMapperBean;
import tools.datasync.basic.util.Md5HashGenerator;

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
public class JvmSyncPumpSender implements Runnable {

    BlockingQueue<String> sendQueue;
    AtomicBoolean isRunning;

    SyncStateInitializer syncStateInitializer = new JDBCSyncStateInitializer();

    SeedProducer seedProducer = null;
    JSONMapperBean jsonMapper = null;
    HashGenerator hashGen = null;
    int messageNumber = 0;

    CountDownLatch beginSeedLatch;

    Logger logger = Logger.getLogger(JvmSyncPumpSender.class.getName());

    public JvmSyncPumpSender(BlockingQueue<String> sendQueue) {

	this.sendQueue = sendQueue;
	this.isRunning = new AtomicBoolean(true);
	this.jsonMapper = JSONMapperBean.getInstance();
	this.hashGen = Md5HashGenerator.getInstance();
    }

    public void setSeedProducer(SeedProducer seedProducer) {
	this.seedProducer = seedProducer;
    }

    @Override
    public void run() {
	isRunning.set(true);

	logger.info("Started JvmSyncPumpSender...");

	syncStateInitializer.setIsRunning(isRunning);
	syncStateInitializer.doSeed();
	// TODO: Send message to the other Peer's receiver "Begin seed"
	SyncMessage syncMessage = new SyncMessage(null, messageNumber++,
		SyncMessageType.BEGIN_SEED.toString(), null, null,
		System.currentTimeMillis());
	String message;
	try {
	    message = jsonMapper.writeValueAsString(syncMessage);
	    this.sendQueue.put(message);
	    logger.info("Completed seeding, send message to the peer that we're ready to receive");

	    // TODO: Wait for "Begin Seed" message from other peer
	    logger.info("Waiting for received message from peer that we're ready to send");
	    beginSeedLatch.await();
	    logger.info("Peer sent begin seed, ready to send messages");

	} catch (Exception e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	while ((!Thread.currentThread().isInterrupted()) && isRunning.get()) {
	    try {
		SeedRecord seed = seedProducer.getNextSeed();
		String payloadJson = jsonMapper.writeValueAsString(seed);
		String paloadHash = hashGen.generate(payloadJson);
		syncMessage = new SyncMessage(seed.getOrigin(),
			messageNumber++, SyncMessageType.SEED.toString(),
			payloadJson, paloadHash, System.currentTimeMillis());
		message = jsonMapper.writeValueAsString(syncMessage);

		logger.info("Sending - " + message);
		this.sendQueue.put(message);

	    } catch (SeedOverException soe) {
		logger.warn(
			"Seed phase is over... Terminating the sender process logic.",
			soe);
		break;
	    } catch (SeedException se) {
		logger.warn("Error while creating seed record.", se);
		break;
	    } catch (JsonGenerationException | JsonMappingException jme) {
		logger.warn("Error while creating JSON.", jme);
		break;
	    } catch (IOException | InterruptedException ioe) {
		logger.warn("Error while creating JSON.", ioe);
		break;
	    }
	}

	try {
	    syncMessage = new SyncMessage(null, messageNumber++,
		    SyncMessageType.SYNC_OVER.toString(), null, null,
		    System.currentTimeMillis());
	    message = jsonMapper.writeValueAsString(syncMessage);

	    logger.info("Sending - " + message);
	    this.sendQueue.put(message);
	} catch (IOException | InterruptedException e) {
	    e.printStackTrace();
	}

	isRunning.set(false);
    }

    public void setBeginSeedLatch(CountDownLatch beginSeedLatch) {
	this.beginSeedLatch = beginSeedLatch;
    }

    public AtomicBoolean isRunning() {
	return isRunning;
    }
}
