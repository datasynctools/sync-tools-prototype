package tools.datasync.basic.seed;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import tools.datasync.basic.dao.DbTableComparator;
import tools.datasync.basic.dao.GenericDao;
import tools.datasync.basic.model.Ids;
import tools.datasync.basic.sync.SyncOrchestrationManager;
import tools.datasync.basic.sync.SyncPeer;
import tools.datasync.basic.sync.SyncSession;
import tools.datasync.basic.sync.pump.JvmSyncPumpFactory;
import tools.datasync.basic.sync.pump.SyncPumpFactory;

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
public class SeedIntegrationTest {

	private SyncOrchestrationManager syncOrchMgr;
	private SyncPumpFactory pumpFactoryA;
	private SyncPumpFactory pumpFactoryB;
	Logger logger = Logger.getLogger(SeedIntegrationTest.class.getName());

	@Before
	public void init() {

		FileUtils.deleteQuietly(new File("db-A"));
		FileUtils.deleteQuietly(new File("db-B"));

		logger.info("init...");
		System.out.println("Test print sysout....");
		SyncPeer syncPeerA = new SyncPeer("A");
		SyncPeer syncPeerB = new SyncPeer("B");

		BlockingQueue<String> a2bQueue = new LinkedBlockingQueue<String>();
		BlockingQueue<String> b2aQueue = new LinkedBlockingQueue<String>();

		pumpFactoryA = new JvmSyncPumpFactory(syncPeerA, syncPeerB, a2bQueue, b2aQueue);
		pumpFactoryB = new JvmSyncPumpFactory(syncPeerB, syncPeerA, b2aQueue, a2bQueue);
		syncOrchMgr = new SyncOrchestrationManager(pumpFactoryA, pumpFactoryB);

		
	}

	@Test
	public void firstTest() {

		try {
			logger.info("first test...");
			SyncSession syncSession = syncOrchMgr.createSession();

			syncSession.doSync();

			// TODO: Verify the state of both databases.
			// Verify the state in A and B for User and State tables.
			GenericDao sourceDao = ((JvmSyncPumpFactory)pumpFactoryA).getSourceDao();
			GenericDao targetDao = ((JvmSyncPumpFactory)pumpFactoryA).getTargetDao();
			DbTableComparator comparator = new DbTableComparator(sourceDao, targetDao);
			
			comparator.compare(Ids.Table.CONTACT);
			comparator.compare(Ids.Table.CONTACT_LINK);
			comparator.compare(Ids.Table.WORK_HISTORY);
			comparator.compare(Ids.Table.SYNC_STATE);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
