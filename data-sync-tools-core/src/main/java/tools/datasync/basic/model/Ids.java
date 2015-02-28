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
 * @since   20-Nov-2014
 */

package tools.datasync.basic.model;

public class Ids {

    public static interface Table {

	public static final String CONTACT = "org.Contact";
	public static final String CONTACT_LINK = "org.ContactLink";
	public static final String WORK_HISTORY = "org.WorkHistory";

	public static final String SYNC_ENTITY = "seed.SyncEntity";
	public static final String SYNC_PEER = "seed.SyncPeer";
	public static final String SYNC_STATE = "seed.SyncState";
    }

    public static class KeyColumn {

	public static final String PK_CONTACT = "CONTACTID";
	public static final String PK_CONTACT_LINK = "SOURCECONTACTID, TARGETCONTACTID, WORKHISTORYID";
	public static final String PK_WORK_HISTORY = "WORKHISTORYID";

	public static final String PK_SYNC_ENTITY = "ENTITYID";
	public static final String PK_SYNC_PEER = "PEERID";
	public static final String PK_SYNC_STATE = "ENTITYID, RECORDID";

    }
}
