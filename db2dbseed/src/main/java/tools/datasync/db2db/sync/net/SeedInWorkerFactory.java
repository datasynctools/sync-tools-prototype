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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tools.datasync.db2db.model.SeedRecord;
import tools.datasync.db2db.seed.SeedConsumer;
import tools.datasync.db2db.util.ExceptionHandler;

@Service
public class SeedInWorkerFactory {

	@Autowired
	private ExceptionHandler exceptionHandler;

	@Autowired
	private SeedConsumer seedConsumer;

	// Factory method is required here to avoid bean auto-wiring in each worker instance
	public SeedInWorker newWorker(SeedRecord seed){
		
		return new SeedInWorker(seed, seedConsumer, exceptionHandler);
	}
}
