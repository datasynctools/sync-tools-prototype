/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.seed.SeedException;
import tools.datasync.basic.seed.SeedOverException;
import tools.datasync.basic.seed.SeedProducer;
import tools.datasync.basic.util.JSONMapperBean;

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
public class JvmSyncPumpSender implements Runnable, UncaughtExceptionHandler {

    private static final Logger LOG = Logger.getLogger(JvmSyncPumpSender.class
	    .getName());

    private BlockingQueue<String> sendQueue;
    private AtomicBoolean isRunning;

    private AtomicBoolean stopper;

    private SyncStateInitializer syncStateInitializer;

    private SeedProducer seedProducer = null;
    private JSONMapperBean jsonMapper = null;
    private int messageNumber = 0;

    private SenderPresentAcknolwedger senderPresentAcknolwedger = new SenderPresentAcknolwedger();
    private ReceiverPresentAcknolwedger receiverPresentAcknolwedger = new ReceiverPresentAcknolwedger();
    private BothSendersPresentAcknowledger bothSendersPresentAcknowledger = new BothSendersPresentAcknowledger();

    public JvmSyncPumpSender(BlockingQueue<String> sendQueue,
	    SyncStateInitializer syncStateInitializer, AtomicBoolean stopper) {

	this.sendQueue = sendQueue;
	this.stopper = stopper;
	this.isRunning = new AtomicBoolean(true);
	this.jsonMapper = JSONMapperBean.getInstance();
	this.syncStateInitializer = syncStateInitializer;
    }

    public void setSeedProducer(SeedProducer seedProducer) {
	this.seedProducer = seedProducer;
    }

    private void initialze() throws SQLException, IOException,
	    InterruptedException {
	syncStateInitializer.setIsRunning(true);
	syncStateInitializer.doSeed();
	syncStateInitializer.setIsRunning(false);

	// Send message to the other Peer's receiver "Begin seed"
	SyncMessage syncMessage = new SyncMessage(null, messageNumber++,
		SyncMessageType.BEGIN_SEED.toString(), null, null,
		System.currentTimeMillis());
	String message = jsonMapper.writeValueAsString(syncMessage);
	this.sendQueue.put(message);
	LOG.info("Completed seeding, send message to the peer that we're ready to receive");

    }

    private boolean activeMessages() {
	boolean active = (!Thread.currentThread().isInterrupted())
		&& isRunning.get() && !stopper.get()
		&& seedProducer.isRunning();
	return (active);
    }

    private void sendSyncMessage(SeedRecord seed, SyncMessage syncMessage,
	    String message) throws InterruptedException,
	    JsonGenerationException, JsonMappingException, IOException {
	String payloadJson = jsonMapper.writeValueAsString(seed);
	String paloadHash = seed.getRecordHash();
	syncMessage = new SyncMessage(seed.getOrigin(), messageNumber++,
		SyncMessageType.SEED.toString(), payloadJson, paloadHash,
		System.currentTimeMillis());
	message = jsonMapper.writeValueAsString(syncMessage);

	LOG.info("Sending - " + message);
	this.sendQueue.put(message);

    }

    private void sendSyncMessages(SyncMessage syncMessage, String message)
	    throws SeedOverException, SeedException, JsonGenerationException,
	    JsonMappingException, IOException, InterruptedException {
	while (activeMessages()) {

	    // Get next seed
	    SeedRecord seed = seedProducer.getNextSeed();
	    if (seed == null) {
		LOG.info(">>> Seed phase is over... Terminating the sender process logic.");
		isRunning.set(false);
		return;
	    }

	    sendSyncMessage(seed, syncMessage, message);

	}

    }

    private void runPostAckMain() throws SeedOverException, SeedException,
	    JsonGenerationException, JsonMappingException, IOException,
	    InterruptedException {
	SyncMessage syncMessage = null;
	String message = null;

	sendSyncMessages(syncMessage, message);

	LOG.info("Flag state: Thread interrupted="
		+ Thread.currentThread().isInterrupted() + ", isRunning="
		+ isRunning.get() + ", stopper=" + stopper.get()
		+ ", seedProducer.isRunning=" + seedProducer.isRunning());
	syncMessage = new SyncMessage(null, messageNumber++,
		SyncMessageType.SYNC_OVER.toString(), null, null,
		System.currentTimeMillis());
	message = jsonMapper.writeValueAsString(syncMessage);

	LOG.info("Sending - " + message);
	this.sendQueue.put(message);
    }

    private boolean preAckMain() throws SQLException, IOException,
	    InterruptedException {

	initialze();

	if (!receiverPresentAcknolwedger.waitForReceiverAck(isRunning)) {
	    return false;
	}

	if (!senderPresentAcknolwedger.waitForSenderAck(isRunning, stopper)) {
	    return false;
	}

	if (!bothSendersPresentAcknowledger.waitForBothSendersAck(isRunning,
		stopper)) {
	    return false;
	}

	return true;
    }

    public void run() {
	isRunning.set(true);

	LOG.info("Started JvmSyncPumpSender...");
	try {

	    if (!preAckMain()) {
		return;
	    }
	    runPostAckMain();

	} catch (SeedOverException e) {
	    LOG.info("Seed Over from Seed Producer", e);
	    isRunning.set(false);
	    // TODO: Should we throw here ?
	    // throw (new RuntimeException(e));
	} catch (Exception e) {
	    LOG.error("Error in Sender", e);
	    isRunning.set(false);
	    throw (new RuntimeException(e));
	}

	isRunning.set(false);
    }

    public void setAckPairReceiverLatch(CountDownLatch ackPairReceiverLatch) {
	receiverPresentAcknolwedger
		.setAckPairReceiverLatch(ackPairReceiverLatch);
    }

    public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
	senderPresentAcknolwedger.setAckPeerSenderLatch(ackPeerSenderLatch);
    }

    public void setBeginSenderLatch(CountDownLatch beginSenderLatch) {
	bothSendersPresentAcknowledger.setBeginSenderLatch(beginSenderLatch);
    }

    public AtomicBoolean isRunning() {
	return isRunning;
    }

    public void stop() {
	stopper.set(true);
	isRunning.set(false);
    }

    public void uncaughtException(Thread t, Throwable e) {
	LOG.error("Error on thread " + t.getName() + " with " + e.getMessage());
	LOG.info("Initiating Stopper, shutting down");
	stop();
    }
}
