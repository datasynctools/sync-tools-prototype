/**
 * 
 */
package tools.datasync.pump;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.api.utils.SyncMessageQueue;
import tools.datasync.pump.handlers.SyncMessageHandler;
import tools.datasync.seed.SeedConsumer;
import tools.datasync.seed.SeedException;
import tools.datasync.utils.StringUtils;

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
public class SyncPumpReceiver implements Runnable, UncaughtExceptionHandler {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SyncPumpReceiver.class);

    private SyncMessageHandler syncMessageHandler = null;

    private SyncMessageQueue receiveQueue;
    private AtomicBoolean isRunning;
    private SeedConsumer seedConsumer;

    private AtomicBoolean stopper;

    private CountDownLatch ackPairReceiverLatch;
    private CountDownLatch ackPeerSenderLatch;
    private CopyOnWriteArrayList<String> arrayList;
    private NextEntitySignaler nextEntitySignaler;

    public SyncPumpReceiver(SyncMessageQueue receiveQueue, AtomicBoolean stopped) {

	this.receiveQueue = receiveQueue;
	this.stopper = stopped;
	this.isRunning = new AtomicBoolean(true);
    }

    public void setSeedConsumer(SeedConsumer seedConsumer) {
	this.seedConsumer = seedConsumer;
    }

    private boolean checkForStopRequest() {
	if (stopper.get()) {
	    LOG.info("Stop requested, shutting down");
	    isRunning.set(false);
	    return true;
	}
	return false;
    }

    private SyncMessage getMessage() throws InterruptedException {
	SyncMessage message = this.receiveQueue
		.poll(500, TimeUnit.MILLISECONDS);
	return message;
    }

    private boolean continueWhile() {
	return (!Thread.currentThread().isInterrupted()) && isRunning.get();
    }

    private void mainLoop() throws InterruptedException, JsonParseException,
	    JsonMappingException, IOException, SeedException {
	while (continueWhile()) {
	    boolean finishMe = mainLogic();
	    if (finishMe) {
		return;
	    }
	}
    }

    private boolean mainLogic() throws InterruptedException,
	    JsonParseException, JsonMappingException, IOException,
	    SeedException {
	boolean finishMe = checkForStopRequest();
	if (finishMe) {
	    return true;
	}

	SyncMessage message = getMessage();

	if (message != null) {
	    finishMe = handleMessage(message);
	    if (finishMe) {
		return true;
	    }
	}
	// TODO is there an error case here where the message is empty?
	return false;
    }

    private boolean handleMessage(SyncMessage message)
	    throws JsonParseException, JsonMappingException, IOException,
	    SeedException {

	return syncMessageHandler.handle(message);

    }

    public void run() {
	LOG.info("Started sync receiver: {}", this.toString());
	try {

	    if (syncMessageHandler == null) {
		syncMessageHandler = new SyncMessageHandler(ackPeerSenderLatch,
			arrayList, seedConsumer, nextEntitySignaler);
	    }

	    ackPairReceiverLatch.countDown();
	    LOG.info("Acknowledged Sender-Receiver pair");

	    mainLoop();

	} catch (Throwable e) {
	    LOG.error("Error while receiving messages", e);
	    isRunning.set(false);
	    stop();
	}

	LOG.info("Finished sync receiver");
	isRunning.set(false);
    }

    public AtomicBoolean isRunning() {
	return isRunning;
    }

    public void setAckPairReceiverLatch(CountDownLatch ackPairReceiverLatch) {
	this.ackPairReceiverLatch = ackPairReceiverLatch;
    }

    public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
	this.ackPeerSenderLatch = ackPeerSenderLatch;
    }

    public void setArrayList(CopyOnWriteArrayList<String> arrayList) {
	this.arrayList = arrayList;
    }

    public SyncMessageQueue getQueue() {
	return receiveQueue;
    }

    public void stop() {
	stopper.set(true);
	isRunning.set(false);
    }

    public void setNextEntitySignaler(NextEntitySignaler nextEntitySignaler) {
	this.nextEntitySignaler = nextEntitySignaler;
    }

    public void uncaughtException(Thread t, Throwable e) {
	LOG.error("Error on thread " + t.getName() + " with " + e.getMessage());
	stop();
    }

    private void addQueues(StringBuilder answer) {
	answer.append("receiveQueue=");
	answer.append(receiveQueue.toString());
	answer.append(", ");
	answer.append("receiveQueueClass=");
	answer.append(StringUtils.getSimpleName(receiveQueue));
	answer.append(", ");
	answer.append("queueInstanceHashCode=");
	answer.append(receiveQueue.hashCode());
	answer.append(", ");
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	addQueues(answer);
	answer.append("seedConsumer=");
	answer.append(seedConsumer.toString());
	answer.append(", ");
	answer.append("nextEntitySignaler=");
	answer.append(nextEntitySignaler.toString());
	answer.append("}");
	return (answer.toString());
    }

}
