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
package tools.datasync.basic.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class Md5HashGenerator implements HashGenerator {

    private static Md5HashGenerator instance = null;
    private MessageDigest messageDigest = null;
    private Logger logger = Logger.getLogger(Md5HashGenerator.class.getName());

    private Md5HashGenerator() {
	try {
	    messageDigest = MessageDigest.getInstance("MD5");
	} catch (NoSuchAlgorithmException e) {
	    logger.warn("Error while initializing Hash Generator.", e);
	}
    }

    public static synchronized Md5HashGenerator getInstance() {
	if (instance == null) {
	    instance = new Md5HashGenerator();
	}
	return instance;
    }

    public String generate(String data) {
	try {
	    byte[] input = toBytes(data);
	    logger.debug("Generating Hash for bytes: " + convertByteArrayToHexString(input));
	    messageDigest.update(input);
	    byte[] digest = messageDigest.digest(input);
	    return (convertByteArrayToHexString(digest));
	} catch (Exception e) {
	    logger.warn("Error while generating checksum", e);
	    return null;
	}
    }
    
    public boolean validate(String data, String hash) {
	String newHash = generate(data);
	return newHash.equals(hash);
    }
    
    public static byte[] toBytes(String str) {
		char[] buffer = str.toCharArray();
		byte[] b = new byte[buffer.length << 1];
		for(int i = 0; i < buffer.length; i++) {
			int bpos = i << 1;
			b[bpos] = (byte) ((buffer[i] & 0xFF00) >> 8);
			b[bpos + 1] = (byte) (buffer[i] & 0x00FF);
		}
    	return b;
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
	StringBuffer stringBuffer = new StringBuffer();
	for (int i = 0; i < arrayBytes.length; i++) {
	    stringBuffer.append(Integer.toString(
		    (arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
	}
	return stringBuffer.toString();
    }

}
