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

import org.springframework.beans.factory.annotation.Autowired;

import tools.datasync.db2db.model.SeedRecord;
import tools.datasync.db2db.util.SeedQueue;

public class SyncManagerImpl implements SyncManager {

	@Autowired
	SeedQueue queue;

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.sync.SyncManager#beginSync(tools.datasync.db2db.sync.SyncPeer)
	 */
	public void beginSync(SyncPeer peer) {
		
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.sync.SyncManager#seedIn(tools.datasync.db2db.model.SeedRecord)
	 */
	public void seedIn(SeedRecord seed) {
		
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.sync.SyncManager#seedOut(tools.datasync.db2db.model.SeedRecord)
	 */
	public void seedOut(SeedRecord seed) {
		
	}

}