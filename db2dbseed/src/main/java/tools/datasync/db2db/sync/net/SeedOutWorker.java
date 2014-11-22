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
* @copyright datasync.tools
* @version 1.0
* @since   14-Nov-2014
*/

package tools.datasync.db2db.sync.net;

import java.util.logging.Level;

import tools.datasync.db2db.model.SeedRecord;
import tools.datasync.db2db.net.Connection;
import tools.datasync.db2db.net.NetException;
import tools.datasync.db2db.net.SyncMessage;
import tools.datasync.db2db.net.SyncMessageType;
import tools.datasync.db2db.util.ExceptionHandler;
import tools.datasync.db2db.util.HashGenerator;
import tools.datasync.db2db.util.JSONMapperBean;

public class SeedOutWorker implements Runnable {
	
	private SeedRecord seed;
	private Connection connection;
	private ExceptionHandler exceptionHandler;
	private JSONMapperBean jsonMapper;
	private HashGenerator hashGenerator;

	protected SeedOutWorker(SeedRecord record, Connection connection, 
			ExceptionHandler exceptionHandler, JSONMapperBean jsonMapper, HashGenerator hashGenerator) {

		this.seed = record;
		this.connection = connection;
		this.exceptionHandler = exceptionHandler;
		this.jsonMapper = jsonMapper;
		this.hashGenerator = hashGenerator;
	}

	public void run() {
		try {
			String originId = seed.getOrigin();
			String messageType = SyncMessageType.SEED.toString();
			String payloadJson = jsonMapper.writeValueAsString(seed);
			String paloadHash = hashGenerator.generate(payloadJson);
			long timestamp = System.currentTimeMillis();
			SyncMessage syncMessage = new SyncMessage(originId, 0, messageType, payloadJson, paloadHash, timestamp);
			
			if(connection.checkOutboundConnection()){
				connection.send(syncMessage);
			} else {
				throw new NetException("Outbound connection failed.");
			}

		} catch (Throwable th) {
			exceptionHandler.handle(th, Level.WARNING, "Exception while processing seed out entry", seed);
			// TODO: how to re-enqueue this failed message ?
		}
	}
}
