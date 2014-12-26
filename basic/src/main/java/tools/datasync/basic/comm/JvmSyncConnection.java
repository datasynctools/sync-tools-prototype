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
package tools.datasync.basic.comm;

import java.io.IOException;

import org.apache.log4j.Logger;

import tools.datasync.basic.sync.SyncManager;
import tools.datasync.basic.util.JSONMapperBean;

public class JvmSyncConnection implements SyncConnection {

    private SyncManager me;
    private SyncConnection other;
    private JSONMapperBean jsonMapper;
    private Logger logger = Logger.getLogger(JvmSyncConnection.class.getName());
    
    public JvmSyncConnection(SyncManager me) {
        this.me = me;
        this.jsonMapper = JSONMapperBean.getInstance();
    }
    
    public void setOtherConnection(SyncConnection other){
        this.other = other;
    }
    
    public void initiate() {
        
    }

    public int send(SyncMessage message) {
        
        try {
            String jsonData = jsonMapper.writeValueAsString(message);
            other.onData(jsonData);
        } catch (IOException e) {
            logger.warn("Unable to send message to peer", e);
        }
        return 1;
    }

    public void onData(String data) {
        
        try {
            SyncMessage message = jsonMapper.readValue(data, SyncMessage.class);
            me.onData(message);
        } catch (IOException e) {
            logger.warn("Unable to parse message from peer", e);
        }
    }

    public boolean checkOutboundConnection() {
        return true;
    }

    public boolean checkInboundConnection() {
        return true;
    }

}
