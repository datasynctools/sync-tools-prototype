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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.SerializationUtils;

import tools.datasync.db2db.sync.DataHandler;
import tools.datasync.db2db.util.ExceptionHandler;

public class TCPConnection implements Connection {

	private ServerSocket serverSocket; // TODO: new runnable to listen from peer and receive data
	private Socket clientSocket; // TODO: new runnable to keep trying peer connection and send data
	
	@Value("${peer.tcp.ip}")
	private String syncPeerIp;
	@Value("${peer.tcp.port}")
	private int syncPeerPort;
	
	private List<DataHandler> handlers;
	
	@Autowired
	ExceptionHandler exceptionHandler;
	
	public TCPConnection(@Value("${server.tcp.port}") int serverPort) {
		try {
			serverSocket = new ServerSocket();
			handlers = new ArrayList<DataHandler>();
		} catch (IOException ex) {
			exceptionHandler.handle(ex, Level.SEVERE, "Exception occured while creating server socket", serverPort);
		}
	}
	
	public int send(byte[] data) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.net.Connection#onData()
	 */
	public void onData(byte[] data) {
		try{
			SyncMessage syncMessage = (SyncMessage) SerializationUtils.deserialize(data);
			
			for(DataHandler handler : handlers){
				try{
					// TODO: This can be very slow... Performance hack
					handler.onData(syncMessage.getPayload());
					
				} catch (Exception ex){
					// Handle exception...
					exceptionHandler.handle(ex, Level.WARNING, "Exception occured while invoking handler", data, handler);
				}
			}
		} catch (Exception ex) {
			// Handle exception...
			exceptionHandler.handle(ex, Level.WARNING, "Exception occured while de-serializing object", data);
		}
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.net.Connection#addDataHandler(tools.datasync.db2db.sync.DataHandler)
	 */
	public boolean addDataHandler(DataHandler handler) {
		
		return handlers.add(handler);
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.net.Connection#removeDataHandler(tools.datasync.db2db.sync.DataHandler)
	 */
	public boolean removeDataHandler(DataHandler handler) {
		
		return handlers.remove(handler);
	}

	/* (non-Javadoc)
	 * @see tools.datasync.db2db.net.Connection#getDataHandlers()
	 */
	public List<DataHandler> getDataHandlers() {
		return handlers;
	}

}
