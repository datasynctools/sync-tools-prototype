/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.model.SeedRecord;
import tools.datasync.basic.seed.SeedConsumer;
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
public class JvmSyncPumpReceiver implements Runnable, UncaughtExceptionHandler {

	BlockingQueue<String> receiveQueue;
	Logger logger = Logger.getLogger(JvmSyncPumpReceiver.class);
	AtomicBoolean isRunning;
	JSONMapperBean jsonMapper;
	SeedConsumer seedConsumer;

	private AtomicBoolean stopper;

	private CountDownLatch ackPairReceiverLatch;
	private CountDownLatch ackPeerSenderLatch;

	public JvmSyncPumpReceiver(BlockingQueue<String> receiveQueue, AtomicBoolean stopped) {

		this.receiveQueue = receiveQueue;
		this.stopper = stopped;
		this.isRunning = new AtomicBoolean(true);
		this.jsonMapper = JSONMapperBean.getInstance();
	}

	public void setSeedConsumer(SeedConsumer seedConsumer) {
		this.seedConsumer = seedConsumer;
	}

	@Override
	public void run() {
		try {

			ackPairReceiverLatch.countDown();
			logger.info("Acknowledged Receiver Pair");
			
			while ((!Thread.currentThread().isInterrupted()) && isRunning.get()) {
				if (stopper.get()) {
					logger.info("Stop requested, shutting down");
					break;
				}
				String message;
				// message = this.receiveQueue.take();
				message = this.receiveQueue.poll(500, TimeUnit.MILLISECONDS);

				if (message == null) {
					// TODO: Try sleep for 100 ms later
					// logger.error("Received message is null, this is unexpected");
					continue;
				}

				logger.info("Received Sync Message: " + message);

				SyncMessage syncMessage = jsonMapper.readValue(message, SyncMessage.class);

				if (SyncMessageType.SEED.equals(syncMessage.getMessageType())) {

					// TODO: process this seed message
					SeedRecord seed = jsonMapper.readValue(syncMessage.getPayloadJson(), SeedRecord.class);
					seedConsumer.consume(seed);

				} else if (SyncMessageType.SYNC_OVER.equals(syncMessage.getMessageType())) {
					break;
				} else if (SyncMessageType.BEGIN_SEED.equals(syncMessage.getMessageType())) {
					// TODO: signal the sender to start sending
					ackPeerSenderLatch.countDown();
					logger.info("Acknowledged Sender Peer with ackPeerSenderLatch" + ackPeerSenderLatch);
					
					// logger.info("Received begin seed from receiving pair. beginSeedLatch"
					// + ackPeerSenderLatch);
				}
				// catch (IOException ex) {
				// logger.warn("Error while consuming message." + ex);
				// isRunning.set(false);
				// } catch (SeedException ex) {
				// logger.warn("Error while parsing message." + ex);
				// isRunning.set(false);
				// } catch (Throwable e) {
				// logger.fatal("Fatal error while parsing message." + e);
				// isRunning.set(false);
				// }

			}

		} catch (Throwable e) {
			logger.fatal("Error while receiving messages: " + e);
			isRunning.set(false);
			stop();
		}

		logger.info("Finished sync receiver");
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

	public BlockingQueue<String> getQueue() {
		return this.receiveQueue;
	}

	public void stop() {
		stopper.set(true);
		isRunning.set(false);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		logger.error("Error on thread " + t.getName() + " with " + e.getMessage());
		stop();
	}
}
