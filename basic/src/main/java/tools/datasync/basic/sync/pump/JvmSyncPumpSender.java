/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

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
 * @since   29-Nov-2014
 */
public class JvmSyncPumpSender implements Runnable {

    BlockingQueue<String> sendQueue;
    AtomicBoolean isRunning;
    
    Logger logger = Logger.getLogger(JvmSyncPumpSender.class.getName());
    
    public JvmSyncPumpSender(BlockingQueue<String> sendQueue){
    
        this.sendQueue = sendQueue;
        this.isRunning = new AtomicBoolean(true);
    }
    
    @Override
    public void run() {
        //TODO: Add sending logic
        isRunning.set(true);
        
        logger.info("put 1");
        logger.info("put 2");
        logger.info("put 3");
        logger.info("put 4");
        
        isRunning.set(false);
    }

    public AtomicBoolean isRunning(){
        return isRunning;
    }
}
