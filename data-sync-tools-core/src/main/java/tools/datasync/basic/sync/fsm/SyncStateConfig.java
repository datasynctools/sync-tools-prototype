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
 * @since   15-Nov-2014
 */

package tools.datasync.basic.sync.fsm;

import static tools.datasync.basic.comm.SyncMessageType.ACK;
import static tools.datasync.basic.comm.SyncMessageType.ANALYSIS_OVER;
import static tools.datasync.basic.comm.SyncMessageType.APPLY_CHANGES;
import static tools.datasync.basic.comm.SyncMessageType.APPLY_CHANGES_OVER;
import static tools.datasync.basic.comm.SyncMessageType.BEGIN_SEED;
import static tools.datasync.basic.comm.SyncMessageType.BEGIN_SYNC;
import static tools.datasync.basic.comm.SyncMessageType.CONFLICT_RESOLUTION;
import static tools.datasync.basic.comm.SyncMessageType.NACK;
import static tools.datasync.basic.comm.SyncMessageType.SEED;
import static tools.datasync.basic.comm.SyncMessageType.SEED_OVER_FOLLOW;
import static tools.datasync.basic.comm.SyncMessageType.SEED_OVER_INIT;
import static tools.datasync.basic.comm.SyncMessageType.SYNC_OVER;
import static tools.datasync.basic.comm.SyncMessageType.UNKNOWN;
import static tools.datasync.basic.sync.fsm.SyncStateElement.ANALYZING;
import static tools.datasync.basic.sync.fsm.SyncStateElement.INVALID;
import static tools.datasync.basic.sync.fsm.SyncStateElement.READY;
import static tools.datasync.basic.sync.fsm.SyncStateElement.RESOLVING;
import static tools.datasync.basic.sync.fsm.SyncStateElement.SEEDING;
import static tools.datasync.basic.sync.fsm.SyncStateElement.SYNCING;
import static tools.datasync.basic.sync.fsm.SyncStateElement.WAITING;

public class SyncStateConfig {

    private boolean initiated = false;

    public SyncStateConfig() {
	init();
	initiated = true;
    }

    public boolean isReady() {
	return initiated;
    }

    private void initReady() {
	READY.addTransition(new SyncStateTransition(READY, BEGIN_SYNC, READY));
	READY.addTransition(new SyncStateTransition(READY, BEGIN_SEED, SEEDING));
	READY.addTransition(new SyncStateTransition(READY, UNKNOWN, READY));
    }

    private void initSeeding() {
	SEEDING.addTransition(new SyncStateTransition(SEEDING, SEED, SEEDING));
	SEEDING.addTransition(new SyncStateTransition(SEEDING, ACK, SEEDING));
	SEEDING.addTransition(new SyncStateTransition(SEEDING, NACK, SEEDING));
	SEEDING.addTransition(new SyncStateTransition(SEEDING, SEED_OVER_INIT,
		WAITING));
	SEEDING.addTransition(new SyncStateTransition(SEEDING,
		SEED_OVER_FOLLOW, ANALYZING));
	SEEDING.addTransition(new SyncStateTransition(SEEDING, UNKNOWN, INVALID));
    }

    private void initWaiting() {
	WAITING.addTransition(new SyncStateTransition(WAITING, ANALYSIS_OVER,
		WAITING));
	WAITING.addTransition(new SyncStateTransition(WAITING,
		CONFLICT_RESOLUTION, WAITING));
	WAITING.addTransition(new SyncStateTransition(WAITING, APPLY_CHANGES,
		SYNCING));
	WAITING.addTransition(new SyncStateTransition(WAITING, UNKNOWN, INVALID));
    }

    private void initResolving() {
	RESOLVING.addTransition(new SyncStateTransition(RESOLVING, ACK,
		RESOLVING));
	RESOLVING.addTransition(new SyncStateTransition(RESOLVING, NACK,
		RESOLVING));
	RESOLVING.addTransition(new SyncStateTransition(RESOLVING,
		APPLY_CHANGES, SYNCING));
	RESOLVING.addTransition(new SyncStateTransition(RESOLVING, UNKNOWN,
		INVALID));

    }

    private void initSyncing() {
	SYNCING.addTransition(new SyncStateTransition(SYNCING,
		APPLY_CHANGES_OVER, SYNCING));
	SYNCING.addTransition(new SyncStateTransition(SYNCING, SYNC_OVER, READY));
	SYNCING.addTransition(new SyncStateTransition(SYNCING, UNKNOWN, INVALID));
    }

    private void initAnalyzing() {
	ANALYZING.addTransition(new SyncStateTransition(ANALYZING,
		ANALYSIS_OVER, RESOLVING));
	ANALYZING.addTransition(new SyncStateTransition(ANALYZING, UNKNOWN,
		INVALID));
    }

    public void init() {

	initReady();

	initSeeding();

	initWaiting();

	initResolving();

	initAnalyzing();

	initSyncing();

	// TODO Is this necessary to have an invalid state? How is this used?
	INVALID.addTransition(new SyncStateTransition(INVALID, SYNC_OVER, READY));
    }
}
