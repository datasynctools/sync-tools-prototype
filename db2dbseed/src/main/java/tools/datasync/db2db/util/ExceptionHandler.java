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

package tools.datasync.db2db.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

@Service
public class ExceptionHandler {

	private Logger logger = Logger.getLogger(ExceptionHandler.class.getName());
	
	public ExceptionHandler() {
		logger.info("Starting ExceptionHandler...");
	}
	
	public void handle(Throwable ex, Level level, String message, Object... params){
		
		// TODO: Log all caused by messages also...
		// TODO: Search tools.datasync.db2db package method and log 'at' here...
		StackTraceElement[] stackTraceElements = ex.getStackTrace();
		StackTraceElement top = stackTraceElements[0];
		
		String clazz = top.getClassName();
		String method = top.getMethodName();
		int line = top.getLineNumber();
		
		StringBuffer sb = new StringBuffer();
		sb.append(clazz);
		sb.append('.');
		sb.append(method);
		sb.append('(');
		sb.append(line);
		sb.append(") : ");
		sb.append(message);
		sb.append(". ");
		sb.append(ex.getMessage());
		sb.append('\n');
		
		logger.log(level, sb.toString());
	}
}
