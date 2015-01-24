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
import tools.datasync.basic.dao.JDBCSyncStateInitializer;
import tools.datasync.basic.model.SeedRecord;
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
public class JvmSyncPumpSender implements Runnable, UncaughtExceptionHandler {

	BlockingQueue<String> sendQueue;
	AtomicBoolean isRunning;

	private AtomicBoolean stopper;

	SyncStateInitializer syncStateInitializer;

	SeedProducer seedProducer = null;
	JSONMapperBean jsonMapper = null;
	HashGenerator hashGen = null;
	int messageNumber = 0;

	CountDownLatch ackPairReceiverLatch;
	CountDownLatch ackPeerSenderLatch;
	CountDownLatch beginSenderLatch;

	Logger logger = Logger.getLogger(JvmSyncPumpSender.class.getName());

	public JvmSyncPumpSender(BlockingQueue<String> sendQueue, AtomicBoolean stopper) {

		this.sendQueue = sendQueue;
		this.stopper = stopper;
		this.isRunning = new AtomicBoolean(true);
		this.jsonMapper = JSONMapperBean.getInstance();
		this.hashGen = Md5HashGenerator.getInstance();
		this.syncStateInitializer = new JDBCSyncStateInitializer();
	}

	public void setSeedProducer(SeedProducer seedProducer) {
		this.seedProducer = seedProducer;
	}

	@Override
	public void run() {
		isRunning.set(true);

		logger.info("Started JvmSyncPumpSender...");
		try {

			// try {
			// Populate the SyncState table from all data tables...
			syncStateInitializer.setGenericDao(seedProducer.getGenericDao());
			syncStateInitializer.setIsRunning(true);
			syncStateInitializer.doSeed();
			syncStateInitializer.setIsRunning(false);

			// TODO: Send message to the other Peer's receiver "Begin seed"
			SyncMessage syncMessage = new SyncMessage(null, messageNumber++, SyncMessageType.BEGIN_SEED.toString(), null, null,
					System.currentTimeMillis());
			String message = jsonMapper.writeValueAsString(syncMessage);
			this.sendQueue.put(message);
			logger.info("Completed seeding, send message to the peer that we're ready to receive");

			// Confirm pair receiver is up-and-running
			ackPairReceiverLatch.await();
			
			

			// TODO: Wait for "Begin Seed" message from other peer
			logger.info("Waiting for peer sender message so we can send using ackPeerSenderLatch " + ackPeerSenderLatch);
			boolean acknowledged = ackPeerSenderLatch.await(500, TimeUnit.MILLISECONDS);
			while (!acknowledged) {
				if (stopper.get()) {
					logger.info("Stop requested, shutting down");
					isRunning.set(false);
					return;
				}
				logger.info("No acknowledge received on waiting for beginSeedLatch " + ackPeerSenderLatch);
				logger.info("No sender acknowledge on ackPeerSenderLatch " + ackPeerSenderLatch);
				acknowledged = ackPeerSenderLatch.await(500, TimeUnit.MILLISECONDS);
			}
			logger.info("Peersender sent begin seed, ready to send messages");

			// Count down beginSenderLatch indicating this sender is ready.
			logger.info(">>> Counting down beginSenderLatch indicating this sender is ready");
			beginSenderLatch.countDown();
						
			// Wait on beginSenderLatch until both senders are ready.
			logger.info(">>> Waiting on beginSenderLatch until both senders are ready");
			beginSenderLatch.await();
						
			// } catch (Exception e1) {
			// logger.warn("Exception while begin seed...", e1);
			// isRunning.set(false);
			// stopper.set(true);
			// }

			while ((!Thread.currentThread().isInterrupted()) 
					&& isRunning.get() 
					&& !stopper.get()
					&& seedProducer.isRunning()) {
				// try {
				
				// Get next seed
				SeedRecord seed = seedProducer.getNextSeed();
				if(seed == null){
					logger.info(">>> Seed phase is over... Terminating the sender process logic.");
					isRunning.set(false);
					break;
				}
				
				String payloadJson = jsonMapper.writeValueAsString(seed);
				String paloadHash = hashGen.generate(payloadJson);
				syncMessage = new SyncMessage(seed.getOrigin(), messageNumber++, SyncMessageType.SEED.toString(), payloadJson, paloadHash,
						System.currentTimeMillis());
				message = jsonMapper.writeValueAsString(syncMessage);

				logger.info("Sending - " + message);
				this.sendQueue.put(message);

				// } catch (SeedOverException soe) {
				// logger.warn(
				// "Seed phase is over... Terminating the sender process logic.",
				// soe);
				// break;
				// } catch (SeedException se) {
				// logger.warn("Error while creating seed record.", se);
				// break;
				// } catch (JsonGenerationException | JsonMappingException jme)
				// {
				// logger.warn("Error while creating JSON.", jme);
				// break;
				// } catch (IOException | InterruptedException ioe) {
				// logger.warn("Error while creating JSON.", ioe);
				// break;
				// }
			}

			logger.info("Flag state: Thread interrupted="+Thread.currentThread().isInterrupted() +
					", isRunning="+ isRunning.get() +
					", stopper="+ stopper.get() +
					", seedProducer.isRunning="+ seedProducer.isRunning());
			// try {
			syncMessage = new SyncMessage(null, messageNumber++, SyncMessageType.SYNC_OVER.toString(), null, null,
					System.currentTimeMillis());
			message = jsonMapper.writeValueAsString(syncMessage);

			logger.info("Sending - " + message);
			this.sendQueue.put(message);

			// } catch (IOException | InterruptedException e) {
			// e.printStackTrace();
			// }
		} catch (SeedOverException e) {
			logger.info("Seed Over from Seed Producer", e);
			isRunning.set(false);
			//TODO: Should we throw here ?
			//throw (new RuntimeException(e));
		} catch (Exception e) {
			logger.error("Error in Sender", e);
			isRunning.set(false);
			throw (new RuntimeException(e));
		}

		isRunning.set(false);
	}

	public void setAckPairReceiverLatch(CountDownLatch ackPairReceiverLatch) {
		this.ackPairReceiverLatch = ackPairReceiverLatch;
	}

	public void setAckPeerSenderLatch(CountDownLatch ackPeerSenderLatch) {
		this.ackPeerSenderLatch = ackPeerSenderLatch;
	}

	public void setBeginSenderLatch(CountDownLatch beginSenderLatch) {
		this.beginSenderLatch = beginSenderLatch;
	}
	
	public AtomicBoolean isRunning() {
		return isRunning;
	}

	public void stop() {
		stopper.set(true);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		logger.error("Error on thread " + t.getName() + " with " + e.getMessage());
		logger.info("Initiating Stopper");
		stop();
	}
}
