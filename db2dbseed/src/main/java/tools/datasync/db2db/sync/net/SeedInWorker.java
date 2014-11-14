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
import tools.datasync.db2db.seed.SeedConsumer;
import tools.datasync.db2db.util.ExceptionHandler;

public class SeedInWorker implements Runnable {
	
	private SeedRecord seed;
	private SeedConsumer seedConsumer;
	private ExceptionHandler exceptionHandler;

	protected SeedInWorker(SeedRecord record, SeedConsumer seedConsumer, ExceptionHandler exceptionHandler) {

		this.seed = record;
		this.seedConsumer = seedConsumer;
		this.exceptionHandler = exceptionHandler;
	}

	public void run() {
		try {
			seedConsumer.consume(seed);

		} catch (Throwable th) {
			exceptionHandler.handle(th, Level.WARNING, "Exception while processing seed in entry");
			// TODO: how to re-enqueue this failed message ?
		}
	}
}
