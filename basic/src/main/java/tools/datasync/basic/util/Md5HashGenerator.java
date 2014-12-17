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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

public class Md5HashGenerator implements HashGenerator {

    private static Md5HashGenerator instance = null;
    private MessageDigest messageDigest = null;
    private NLogger nlogger;

    private Md5HashGenerator() {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            nlogger.log(e, Level.WARNING, "Error while initializing Hash Generator.");
        }
    }

    public static synchronized Md5HashGenerator getInstance() {
        if(instance == null){
            instance = new Md5HashGenerator();
        }
        return instance;
    }

    public String generate(String data) {
        try {
            byte[] input = toBytes(data);
            messageDigest.update(input);
            byte[] digest = messageDigest.digest();
            String checksum = toHexString1(digest);
            return checksum;
        } catch (UnsupportedEncodingException e) {
            nlogger.log(e, Level.WARNING, "Error while generating checksum", data);
            return null;
        }
    }

    public boolean validate(String data, String hash) {
        String newHash = generate(data);
        return newHash.equals(hash);
    }

    private byte[] toBytes(String string) throws UnsupportedEncodingException {
        return string.getBytes("UTF-8");
    }

    private String toHexString1(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    // private String toHexString2(byte[] bytes) {
    // StringBuffer hexString = new StringBuffer();
    // for (int i = 0; i < bytes.length; i++) {
    // String hex = Integer.toHexString(0xff & bytes[i]);
    // if (hex.length() == 1)
    // hexString.append('0');
    // hexString.append(hex);
    // }
    // return hexString.toString();
    // }

}
