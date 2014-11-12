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
package tools.datasync.db2db.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import tools.datasync.db2db.sync.DataHandler;

public class TCPConnection implements Connection {

	private ServerSocket serverSocket; // TODO: new runnable to listen from peer
	private Socket clientSocket; // TODO: new runnable to keep trying peer connection
	
	@Value("${peer.tcp.ip}")
	private String syncPeerIp;
	@Value("${peer.tcp.port}")
	private int syncPeerPort;
	
	public TCPConnection(@Value("${server.tcp.port}") int serverPort) {
		try {
			serverSocket = new ServerSocket();
		} catch (IOException e) {
			// TODO: Log here...
			e.printStackTrace();
		}
	}
	
	public int send(byte[] data) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.net.Connection#onData()
	 */
	public void onData(byte[] data) {
		
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.net.Connection#addDataHandler(tools.datasync.db2db.sync.DataHandler)
	 */
	public boolean addDataHandler(DataHandler handler) {
		return false;
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.net.Connection#removeDataHandler(tools.datasync.db2db.sync.DataHandler)
	 */
	public boolean removeDataHandler(DataHandler handler) {
		return false;
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.net.Connection#getDataHandlers()
	 */
	public List<DataHandler> getDataHandlers() {
		return null;
	}

}
