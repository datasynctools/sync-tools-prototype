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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.datasync.db2db.sync.SyncDataHandler;
import tools.datasync.db2db.util.ExceptionHandler;
import tools.datasync.db2db.util.JSONMapperBean;

@Service
public class TCPConnection implements Connection {

	@Autowired
	private ExceptionHandler exceptionHandler;
	@Autowired
	private JSONMapperBean jsonMapper;
	
	@Value("${peer.tcp.ip}")
	private String syncPeerIp;
	@Value("${peer.tcp.port}")
	private int syncPeerPort;
	@Value("${server.tcp.port}")
	private int serverPort;
	@Value("${peer.name}")
	private String peerName;

	private List<SyncDataHandler> handlers;
	private int messageNumber = 0;

	private final ReentrantLock connectionLock;
	private final AtomicBoolean isConnected;

	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;

	private final Logger logger = Logger.getLogger(TCPConnection.class.getName());

	public TCPConnection() {
		logger.info("Creating TCP Connection service..."+exceptionHandler);
		handlers = new ArrayList<SyncDataHandler>();
		connectionLock = new ReentrantLock();
		isConnected = new AtomicBoolean(false);
	}

	public void initiate() {
		try {
			if("A".equalsIgnoreCase(peerName)){
				if (serverPort == 0) {
					serverPort = 9991;
					syncPeerIp = "127.0.0.1";
					syncPeerPort = 7771;
				}
			} else {
				if (serverPort == 0) {
					serverPort = 7771;
					syncPeerIp = "127.0.0.1";
					syncPeerPort = 9991;
				}
			}
			
			logger.info("Initializing with serverPort=[" + serverPort + "], syncPeerIp=[" + syncPeerIp + "], syncPeerPort=[" + syncPeerPort
					+ "]");

			logger.info("Starting TCP server listener thread...");
			TCPServerListener server_ = new TCPServerListener(serverPort);
			Thread serverThread = new Thread(server_);
			serverThread.start();

			logger.info("Starting TCP client listener thread...");
			TCPClientListener client_ = new TCPClientListener(syncPeerIp, syncPeerPort);
			Thread clientThread = new Thread(client_);
			clientThread.start();

			// Wait until connection is established...
			logger.info("Waiting until connection is established...");
			while (!isConnected.get()) {
				Thread.sleep(1000);
			}
			logger.info("Connection established... Starting the input reader thread...");
			TCPInputReader inputReader = new TCPInputReader(inStream);
			Thread inputReaderThread = new Thread(inputReader);
			inputReaderThread.start();
		} catch (Exception ex) {
			exceptionHandler.handle(ex, Level.SEVERE, "Exception occured while initializing");
		}
	}

	public int send(SyncMessage message) {

		String syncJSON = null;
		try {
			message.setMessageNumber(messageNumber++);
			syncJSON = jsonMapper.writeValueAsString(message);
			logger.info("sending sync message to the peer - length: " + syncJSON.length() +
					", json data: "+ syncJSON);
			outStream.writeUTF(String.valueOf(syncJSON.length()));
			outStream.writeUTF(" ");
			outStream.writeUTF(syncJSON);
		} catch (IOException e) {
			exceptionHandler.handle(e, Level.WARNING, "Unable to jsonify or send sync message.", message);
		}
		return 0;
	}

	public void onData(String jsonData) {
		try {
			SyncMessage syncMessage = jsonMapper.readValue(jsonData, SyncMessage.class);

			for (SyncDataHandler handler : handlers) {
				try {
					// TODO: This can be very slow... Performance hack
					handler.onData(syncMessage);

				} catch (Exception ex) {
					// Handle exception...
					exceptionHandler.handle(ex, Level.WARNING, "Exception occured while invoking handler", jsonData);
				}
			}
		} catch (Exception ex) {
			// Handle exception...
			exceptionHandler.handle(ex, Level.WARNING, "Exception occured while de-serializing object", jsonData);
		}
	}

	public boolean addSyncDataHandler(SyncDataHandler handler) {

		return handlers.add(handler);
	}

	public boolean removeSyncDataHandler(SyncDataHandler handler) {

		return handlers.remove(handler);
	}

	public List<SyncDataHandler> getSyncDataHandlers() {
		return handlers;
	}

	public boolean checkOutboundConnection() {
		// TODO: No method found to check output stream
		return (isConnected.get() && outStream != null);
	}

	public boolean checkInboundConnection() {
		try {
			return (isConnected.get() && inStream != null && inStream.available() >= 0);
		} catch (IOException e) {
			return false;
		}
	}

	private class TCPInputReader implements Runnable {

		private ObjectInputStream inStream_;
		private Logger logger_ = Logger.getLogger(TCPInputReader.class.getName());
		private StringBuffer sbJSON = new StringBuffer();

		public TCPInputReader(ObjectInputStream inStream) {
			this.inStream_ = inStream;
		}

		public void run() {
			// Keep reading while connected with peer.
			while (isConnected.get()) {
				try {
					if (inStream_.available() > 0) {
						// This data can have many or incomplete JSON...
						String data_ = inStream_.readUTF();
						logger_.info("Received json data from peer " + data_);
						sbJSON.append(data_);
						logger_.info("Buffered JSON content: " + sbJSON.toString());
					}
				} catch (IOException e) {
					exceptionHandler.handle(e, Level.WARNING, "Exception while reading data...");
				}
				try {
					if (sbJSON.length() > 0) {
						if (sbJSON.indexOf("{") < 2 || !Character.isDigit(sbJSON.charAt(0))) {
							throw new NetException("Invalid format of received data from peer...");
						}
						String lengthStr = sbJSON.substring(0, sbJSON.indexOf("{"));
						sbJSON.delete(0, sbJSON.indexOf("{"));
						Integer length = Integer.parseInt(lengthStr.trim());
						String unitJSON = sbJSON.substring(0, length);
						sbJSON.delete(0, length);

						logger_.finer("Processing 1 unit JSON data: " + unitJSON);
						onData(unitJSON);
					}
				} catch (NetException e) {
					exceptionHandler.handle(e, Level.WARNING, "Exception while parsing data...", sbJSON.toString());
				}
			}
		}

	}

	private class TCPClientListener implements Runnable {

		private String ip;
		private int port;
		private Socket clientSocket;
		private Logger logger_ = Logger.getLogger(TCPClientListener.class.getName());

		public TCPClientListener(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		public void run() {
			while (!isConnected.get()) {
				try {
					// Wait 1 second to let peer get started.
					logger_.info("Wait 1 second to let peer get started..");
					Thread.sleep(1000);

					// Try connection with peer...
					logger_.info("Try TCP client connection with peer... IP: "+ip+", Port: "+port);
					clientSocket = new Socket(ip, port);
					logger_.info("TCP client connection accepted...");
					if (connectionLock.tryLock()) {
						if (isConnected.get()) {
							logger_.info("Connection is already setup by the server socket.");
						} else {
							inStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
							outStream = new ObjectOutputStream(clientSocket.getOutputStream());
							isConnected.set(true);
						}
					} else {
						logger_.info("Connection lock is already acquired by server socket...");
					}
				} catch (InterruptedException e) {
					exceptionHandler.handle(e, Level.WARNING, "TCPClientListener interrupted");
				} catch (ConnectException ex) {
					exceptionHandler.handle(ex, Level.WARNING, "TCPClientListener unable to connect with server");
				} catch (Exception ex) {
					exceptionHandler.handle(ex, Level.WARNING, "Error in connection.");
				}
			}
		}

	}

	private class TCPServerListener implements Runnable {

		private int serverPort;
		private ServerSocket serverSocket;
		private Logger logger_ = Logger.getLogger(TCPServerListener.class.getName());

		public TCPServerListener(int serverPort) {
			this.serverPort = serverPort;
		}

		public void run() {
			try {
				logger_.info("Starting server socket at port# " + serverPort);
				serverSocket = new ServerSocket(serverPort);
				logger_.info("Waiting on the server socket for connection request from the peer.");
				// Wait for peer to attempt connection...
				Socket socket = serverSocket.accept();
				logger_.info("Connection request arrived from peer IP: " + socket.getInetAddress() + ", Port: " + socket.getPort());
				// See if the connection lock is free...
				if (connectionLock.tryLock()) {
					if (isConnected.get()) {
						logger_.info("Connection is already setup by the client socket.");
					} else {
						inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
						outStream = new ObjectOutputStream(socket.getOutputStream());
						isConnected.set(true);
					}
				} else {
					logger_.info("Connection lock is already acquired by client socket...");
				}

			} catch (IOException ex) {
				exceptionHandler.handle(ex, Level.SEVERE, "Exception occured while creating server socket", serverPort);
			}
		}
	}
}
