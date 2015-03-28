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
package tools.datasync.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import tools.datasync.api.utils.HashGenerator;

public class Md5HashGenerator implements HashGenerator {

    private static Md5HashGenerator instance = null;
    private static Logger LOG = Logger.getLogger(Md5HashGenerator.class
	    .getName());

    private Md5HashGenerator() {
    }

    public static synchronized Md5HashGenerator getInstance() {
	if (instance == null) {
	    instance = new Md5HashGenerator();
	}
	return instance;
    }

    public String generate(String data) {
	try {
	    byte[] digest = DigestUtils.md5(data);
	    return (DigestUtils.md5Hex(digest));
	} catch (Exception e) {
	    LOG.warn("Error while generating checksum on value [" + data + "]",
		    e);
	    return null;
	}
    }

    public boolean validate(String data, String hash) {
	String newHash = generate(data);
	return newHash.equals(hash);
    }

}
