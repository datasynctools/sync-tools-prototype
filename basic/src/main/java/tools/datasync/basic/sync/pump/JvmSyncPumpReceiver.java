/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.seed.SeedConsumer;
import tools.datasync.basic.seed.SeedException;
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
public class JvmSyncPumpReceiver implements Runnable {

    BlockingQueue<String> receiveQueue;
    Logger logger = Logger.getLogger(JvmSyncPumpReceiver.class.getName());
    AtomicBoolean isRunning;
    JSONMapperBean jsonMapper;
    SeedConsumer seedConsumer;

    CountDownLatch beginSeedLatch;

    public JvmSyncPumpReceiver(BlockingQueue<String> receiveQueue) {

	this.receiveQueue = receiveQueue;
	this.isRunning = new AtomicBoolean(true);
	this.jsonMapper = JSONMapperBean.getInstance();
    }

    public void setSeedConsumer(SeedConsumer seedConsumer) {
	this.seedConsumer = seedConsumer;
    }

    @Override
    public void run() {
	while ((!Thread.currentThread().isInterrupted()) && isRunning.get()) {
	    String message = this.receiveQueue.poll();
	    if (message == null) {
		// TODO: Try sleep for 100 ms later
		continue;
	    }

	    logger.info("Received Seed " + message);

	    try {
		SyncMessage syncMessage = jsonMapper.readValue(message,
			SyncMessage.class);

		if (SyncMessageType.SEED.equals(syncMessage.getMessageType())) {

		    // TODO: process this seed message
		    SeedRecord seed = jsonMapper.readValue(
			    syncMessage.getPayloadJson(), SeedRecord.class);
		    seedConsumer.consume(seed);

		} else if (SyncMessageType.SYNC_OVER.equals(syncMessage
			.getMessageType())) {
		    break;
		} else if (SyncMessageType.BEGIN_SEED.equals(syncMessage
			.getMessageType())) {
		    // TODO: signal the sender to start sending
		    beginSeedLatch.countDown();
		    break;
		}
	    } catch (IOException ex) {
		logger.warn("Error while consuming message." + ex);
		isRunning.set(false);
	    } catch (SeedException ex) {
		logger.warn("Error while parsing message." + ex);
		isRunning.set(false);
	    }
	}

	logger.info("Finished sync receiver");
	isRunning.set(false);
    }

    public AtomicBoolean isRunning() {
	return isRunning;
    }

    public void setBeginSeedLatch(CountDownLatch beginSeedLatch) {
	this.beginSeedLatch = beginSeedLatch;
    }

    public BlockingQueue<String> getQueue() {
	return this.receiveQueue;
    }
}
