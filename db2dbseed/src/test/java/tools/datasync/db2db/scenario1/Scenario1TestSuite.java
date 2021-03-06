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
 * @since   10-Nov-2014
 */

package tools.datasync.db2db.scenario1;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SuiteClasses({ PopulateDatabaseTest.class, InitializeConnectionTest.class })
@ContextConfiguration(locations = "classpath:/SpringBeans.xml")
public class Scenario1TestSuite {

	private Logger logger = Logger.getLogger(Scenario1TestSuite.class.getName());
	
	public Scenario1TestSuite() {
		logger.info("Scenario1TestSuite... INIT...");
	}
	
	@Test
	public void testMethod(){
		
		logger.info("test Scenario1TestSuite logger");
		assertEquals("actual", "actual");
	}
}
