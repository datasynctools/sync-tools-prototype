/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.seed.SeedOverException;
import tools.datasync.basic.seed.SeedProducer;
import tools.datasync.basic.util.JsonMapperBean;

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

    private static final Logger LOG = LoggerFactory
	    .getLogger(JvmSyncPumpSender.class);

    private AtomicBoolean isRunning;
    private AtomicBoolean stopper;
    private long messageNumber = 0;

    private SenderPreAckLogic senderPreAckLogic = new SenderPreAckLogic();
    private SenderPostAckLogic senderPostAckLogic = new SenderPostAckLogic();

    public JvmSyncPumpSender(BlockingQueue<String> sendQueue,
	    SyncStateInitializer syncStateInitializer, AtomicBoolean stopper) {

	this.stopper = stopper;
	this.isRunning = new AtomicBoolean(true);
	senderPreAckLogic.setSyncStateInitializer(syncStateInitializer);
	senderPreAckLogic.setSendQueue(sendQueue);

	JsonMapperBean jsonMapper = JsonMapperBean.getInstance();
	senderPreAckLogic.setJsonMapper(jsonMapper);
	senderPostAckLogic.setJsonMapper(jsonMapper);

	senderPostAckLogic.setIsRunning(isRunning);
	senderPostAckLogic.setStopper(stopper);
	senderPostAckLogic.setSendQueue(sendQueue);
    }

    public void setSeedProducer(SeedProducer seedProducer) {
	senderPostAckLogic.setSeedProducer(seedProducer);
    }

    public void run() {
	isRunning.set(true);

	LOG.info("Started JvmSyncPumpSender...");
	try {

	    SenderPreAckLogicResult result = senderPreAckLogic.preAckMain(
		    isRunning, stopper, messageNumber);

	    if (!result.isContinueProcessing()) {
		return;
	    }

	    messageNumber = result.getMessageCount();

	    messageNumber = senderPostAckLogic.runPostAckMain(messageNumber);

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
	senderPreAckLogic.setAckPairReceiverLatch(ackPairReceiverLatch);
    }

    public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
	senderPreAckLogic.setAckPeerSenderLatch(ackPeerSenderLatch);
    }

    public void setBeginSenderLatch(CountDownLatch beginSenderLatch) {
	senderPreAckLogic.setBeginSenderLatch(beginSenderLatch);
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
