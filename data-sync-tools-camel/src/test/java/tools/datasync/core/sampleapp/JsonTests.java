package tools.datasync.core.sampleapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author Upendra Jariya
 * @sponsor Douglas Johnson
 * @version 1.0
 * @since 29-Nov-2014
 */
public class JsonTests {

    private final static Logger LOG = LoggerFactory.getLogger(JsonTests.class);

    //
    // @Test
    // public void simpleJsonTest() {
    //
    // SyncMessage msg = new SyncMessage();
    // msg.setMessageNumber(1);
    // msg.setMessageType("BEGIN_SEED");
    // String jsonString = new Jsonify().toString(msg);
    // LOG.info(jsonString);
    //
    // // String jsonString =
    // //
    // "{ 'syncMessage': {'originId':'','messageNumber':0,'messageType':'BEGIN_SEED','payloadData':{},'paloadHash':'','timestamp':1426981765867} }";
    // ObjectMapper objectMapper = ObjectMapperFactory.getInstance();
    // SyncMessage syncMessage = null;
    // try {
    //
    // syncMessage = objectMapper.readValue(jsonString, SyncMessage.class);
    // } catch (Exception e) {
    // LOG.error("Bad data [{}]", jsonString, e);
    // throw (new RuntimeException("Bad Data", e));
    // }
    //
    // jsonString = new Jsonify().toString(syncMessage);
    // LOG.info("Found message {}", jsonString);
    // Assert.assertTrue("Begin Seed value not correct",
    // "BEGIN_SEED".equals(syncMessage.getMessageType()));
    // }

    @Test
    public void dateTest() throws ParseException {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	Date aDate = dateFormat.parse("1992-1-23 00:00:00");
	long aTime = aDate.getTime();
	LOG.info("A's time {}", aTime);

	Date bDate = dateFormat.parse("1993-1-23 00:00:00");
	long bTime = bDate.getTime();
	LOG.info("B's time {}", bTime);
    }
    //
    // @Test
    // public void simpleEmbeddedObjTest() {
    //
    // SyncMessage msg = new SyncMessage();
    // msg.setMessageNumber(1);
    // msg.setMessageType("BEGIN_SEED");
    // SeedRecord payloadData = new SeedRecord();
    // payloadData.setEntityId("123");
    // // payloadData.setRecordData("some json");
    // msg.setPayloadData(payloadData);
    // String jsonString = new Jsonify().toString(msg);
    // LOG.info(jsonString);
    //
    // // String jsonString =
    // //
    // "{'originId':'','messageNumber':0,'messageType':'BEGIN_SEED','payloadData':{},'paloadHash':'','timestamp':1426981765867 }";
    // ObjectMapper objectMapper = ObjectMapperFactory.getInstance();
    // SyncMessage syncMessage = null;
    // try {
    //
    // syncMessage = objectMapper.readValue(jsonString, SyncMessage.class);
    // } catch (Exception e) {
    // LOG.error("Bad data [{}]", jsonString, e);
    // throw (new RuntimeException("Bad Data", e));
    // }
    //
    // jsonString = new Jsonify().toString(syncMessage);
    // LOG.info("Found message {}", jsonString);
    // Assert.assertTrue("Begin Seed value not correct",
    // "BEGIN_SEED".equals(syncMessage.getMessageType()));
    // Assert.assertTrue("Embedded Data not correct", "some json"
    // .equals(((SeedRecord) syncMessage.getPayloadData())
    // .getRecordData()));
    // }

}
