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

package tools.datasync.basic.sync;

import java.util.UUID;

public class SyncPeer {

    private String peerId = null;
    private String peerName = null;

    public SyncPeer(String peerName) {
	this.peerId = UUID.randomUUID().toString();
	this.peerName = peerName;
    }

    public SyncPeer(String peerName, String peerId) {
	this.setPeerId(peerId);
	this.peerName = peerName;
    }

    public String getPeerId() {
	return peerId;
    }

    public void setPeerId(String peerId) {
	this.peerId = peerId;
    }

    public String getPeerName() {
	return peerName;
    }

    public void setPeerName(String peerName) {
	this.peerName = peerName;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((peerId == null) ? 0 : peerId.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	SyncPeer other = (SyncPeer) obj;
	return (equals(this, other));
    }

    private static boolean equals(SyncPeer me, SyncPeer other) {
	if (me.peerId == null) {
	    if (other.peerId != null)
		return false;
	} else if (!me.peerId.equals(other.peerId))
	    return false;
	return true;

    }

}
