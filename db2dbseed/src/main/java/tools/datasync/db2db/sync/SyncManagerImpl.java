/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author  Upendra Jariya
 * @sponsor Douglas Johnson
 * @version 1.0
 * @since   2014-11-10
 */
package tools.datasync.db2db.sync;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;

import tools.datasync.db2db.model.SeedRecord;
import tools.datasync.db2db.sync.net.SeedInWorker;
import tools.datasync.db2db.sync.net.SeedInWorkerFactory;
import tools.datasync.db2db.sync.net.SeedOutWorker;
import tools.datasync.db2db.sync.net.SeedOutWorkerFactory;
import tools.datasync.db2db.util.ExceptionHandler;

public class SyncManagerImpl implements SyncManager {

	@Autowired
	private SeedQueue queues;
	@Autowired
	private SeedOutWorkerFactory seedOutWorkerFactory;
	@Autowired
	private SeedInWorkerFactory seedInWorkerFactory;
	@Autowired
	private ExceptionHandler exceptionHandler;
	
	private ThreadPoolExecutor seedOutExecutor = null;
	private ThreadPoolExecutor seedInExecutor = null;
	private final ReentrantLock syncLock = new ReentrantLock();
	private SyncPeer currentPeer = null;

	public void initiate() {
		try {
			seedOutExecutor = new ThreadPoolExecutor(1, 1, 100, TimeUnit.MILLISECONDS, queues.getSeedOutQueue());
			seedInExecutor = new ThreadPoolExecutor(1, 1, 100, TimeUnit.MILLISECONDS, queues.getSeedInQueue());

		} catch (Exception ex) {
			exceptionHandler.handle(ex, Level.SEVERE, "Initialization Failure");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.datasync.db2db.sync.SyncManager#beginSync(tools.datasync.db2db.
	 * sync.SyncPeer)
	 */
	public boolean beginSync(SyncPeer peer) {

		if(syncLock.tryLock()){
			this.currentPeer = peer;
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.datasync.db2db.sync.SyncManager#endSync(tools.datasync.db2db.
	 * sync.SyncPeer)
	 */
	public boolean endSync(SyncPeer peer) {

		syncLock.unlock();
		this.currentPeer = null;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.datasync.db2db.sync.SyncManager#seedIn(tools.datasync.db2db.model
	 * .SeedRecord)
	 */
	public void seedIn(SeedRecord seed) {

		SeedInWorker seedInWorker = seedInWorkerFactory.newWorker(seed);
		queues.getSeedInQueue().add(seedInWorker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.datasync.db2db.sync.SyncManager#seedOut(tools.datasync.db2db.model
	 * .SeedRecord)
	 */
	public void seedOut(SeedRecord seed) {

		SeedOutWorker seedOutWorker = seedOutWorkerFactory.newWorker(seed);
		queues.getSeedOutQueue().add(seedOutWorker);
	}

}
