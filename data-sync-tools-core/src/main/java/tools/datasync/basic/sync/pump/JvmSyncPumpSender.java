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

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.seed.SeedOverException;
import tools.datasync.basic.seed.SeedProducer;
import tools.datasync.basic.util.StringUtils;

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

    private JvmSyncPumpSenderSupport support = new JvmSyncPumpSenderSupport();

    public JvmSyncPumpSender(BlockingQueue<SyncMessage> sendQueue,
	    SyncStateInitializer syncStateInitializer,
	    JvmSyncConcurArgs concurArgs) {

	support.initialize(sendQueue, syncStateInitializer, concurArgs);

    }

    public void setSeedProducer(SeedProducer seedProducer) {
	support.setSeedProducer(seedProducer);
    }

    public void run() {
	support.isRunning().set(true);
	LOG.info("Started sync sender: {}", this.toString());
	try {

	    support.runMain();

	} catch (SeedOverException e) {
	    LOG.info("Seed Over from Seed Producer", e);
	    support.isRunning().set(false);
	    // TODO: Should we throw here ?
	    // throw (new RuntimeException(e));
	} catch (Exception e) {
	    LOG.error("Error in Sender", e);
	    support.isRunning().set(false);

	    throw (new RuntimeException(e));
	}
	LOG.info("Finished sync sender");
	support.isRunning().set(false);

    }

    public void setAckPairReceiverLatch(CountDownLatch ackPairReceiverLatch) {
	support.setAckPairReceiverLatch(ackPairReceiverLatch);
    }

    public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
	support.setAckPeerSenderLatch(ackPeerSenderLatch);
    }

    public AtomicBoolean isRunning() {
	return support.isRunning();
    }

    public void stop() {
	support.stop();
    }

    public void uncaughtException(Thread t, Throwable e) {
	LOG.error("Error on thread " + t.getName() + " with " + e.getMessage());
	LOG.info("Initiating Stopper, shutting down");
	stop();
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	answer.append("senderPreAckLogic=");
	answer.append(support.senderPreAckLogic.toString());
	answer.append(", ");
	answer.append("senderPostAckLogic=");
	answer.append(support.senderPostAckLogic.toString());
	answer.append("}");
	return (answer.toString());
    }
}
