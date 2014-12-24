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
 * @since   12-Nov-2014
 */

package tools.datasync.basic.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class NLogger extends Logger {

    protected NLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers){
            logger.removeHandler(handler);
        }
        
        Formatter newFormatter = new LogFormatter();
        ConsoleHandler console = new ConsoleHandler();
        console.setFormatter(newFormatter);
        logger.addHandler(console);
        
        return logger;
    }
}
