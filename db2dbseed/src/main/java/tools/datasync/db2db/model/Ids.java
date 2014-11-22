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

package tools.datasync.db2db.model;

public class Ids {

	public static interface Table {

		public static final String CONTACT = "Contact";
		public static final String CONTACT_LINK = "ContactLink";
		public static final String WORK_HISTORY = "WorkHistory";

		public static final String SYNC_ENTITY = "SyncEntity";
		public static final String SYNC_PEER = "SyncPeer";
		public static final String SYNC_STATE = "SyncState";
	}

	public static class KeyColumn {

		public static final String PK_CONTACT = "contactId";
		public static final String PK_CONTACT_LINK = "contactLinkId";
		public static final String PK_WORK_HISTORY = "workHistoryId";

		public static final String PK_SYNC_ENTITY = "entityId";
		public static final String PK_SYNC_PEER = "peerId";
		public static final String PK_SYNC_STATE = "stateId";
		
		public static String get(String entityName) {
			if (Table.CONTACT.equalsIgnoreCase(entityName)) {
				return PK_CONTACT;

			} else if (Table.CONTACT_LINK.equalsIgnoreCase(entityName)) {
				return PK_CONTACT_LINK;

			} else if (Table.WORK_HISTORY.equalsIgnoreCase(entityName)) {
				return PK_WORK_HISTORY;

			} else if (Table.SYNC_ENTITY.equalsIgnoreCase(entityName)) {
				return PK_SYNC_ENTITY;

			} else if (Table.SYNC_PEER.equalsIgnoreCase(entityName)) {
				return PK_SYNC_PEER;

			} else if (Table.SYNC_STATE.equalsIgnoreCase(entityName)) {
				return PK_SYNC_STATE;

			}
			return null;
		}
	}

	public static class EntityId {

		public static final String UUID_CONTACT = "A157CC02-CC87-4296-947B-613CAABBDB40";
		public static final String UUID_CONTACT_LINK = "38C11B8D-E4F8-410A-B009-0EF8654A4681";
		public static final String UUID_WORK_HISTORY = "48E2F8D3-2A90-4A26-A198-ED649A4EFBA1";

		public static String get(String entityName) {
			if (Table.CONTACT.equalsIgnoreCase(entityName)) {
				return UUID_CONTACT;

			} else if (Table.CONTACT_LINK.equalsIgnoreCase(entityName)) {
				return UUID_CONTACT_LINK;

			} else if (Table.WORK_HISTORY.equalsIgnoreCase(entityName)) {
				return UUID_WORK_HISTORY;

			}
			return null;
		}
	}
}
