/**
 * 
 */
package tools.datasync.basic.sync.pump;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;

import tools.datasync.basic.dao.GenericJDBCDao;
import tools.datasync.basic.seed.DbSeedProducer;
import tools.datasync.basic.sync.SyncPeer;
import tools.datasync.basic.util.StringUtils;

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
public class JvmSyncPumpFactory implements SyncPumpFactory {

    SyncPeer syncPeerMe = null;
    SyncPeer syncPeerOther = null;
    BlockingQueue<String> queueA2B = null;
    BlockingQueue<String> queueB2A = null;

    Logger logger = Logger.getLogger(JvmSyncPumpFactory.class.getName());

    /**
     * @param syncPeerMe
     * @param syncPeerOther
     */
    public JvmSyncPumpFactory(SyncPeer syncPeerMe, SyncPeer syncPeerOther, BlockingQueue<String> queueA2B,
            BlockingQueue<String> queueB2A) {
        super();
        this.syncPeerMe = syncPeerMe;
        this.syncPeerOther = syncPeerOther;
        this.queueA2B = queueA2B;
        this.queueB2A = queueB2A;
    }

    public SyncPump getInstance(PeerMode peerMode) throws InstantiationException {

        try {
            DataSource dataSource = createDataSource("db-"+syncPeerMe.getPeerName(), true);
            GenericJDBCDao genericDao = new GenericJDBCDao();
            genericDao.setDataSource(dataSource);
            prepareDatabase(dataSource);

            DbSeedProducer seedProducer = new DbSeedProducer();
            seedProducer.setGenericDao(genericDao);
            
            BlockingQueue<String> queue = null;
            if(peerMode.A2B.equals(peerMode)){
                queue = queueA2B;
            } else {
                queue = queueB2A;
            }

            JvmSyncPumpSender sender = new JvmSyncPumpSender(queue);
            sender.setSeedProducer(seedProducer);

            //Temp Testing!
            JvmSyncPumpReceiver receiver = new JvmSyncPumpReceiver(queue);

            return new JvmSyncPump(syncPeerMe, syncPeerOther, sender, receiver);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Cannot instantiate JvmSyncPump", ex);
            throw new InstantiationException(ex.getMessage());
        }
    }

    /* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
    /*       Create and populate the databases before trial       */
    /* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
    
    // creates the Apache Derby data source for given DB name
    private DataSource createDataSource(String dbname, boolean create) throws Exception {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName(dbname);
        if (create) {
            ds.setCreateDatabase("create");
        }
        return ds;
    }

    private void prepareDatabase(DataSource dataSource) throws IOException, SQLException {
        try {
            Connection con = dataSource.getConnection();
            
            logger.info("Creating framework database...");
            runSQLScript(con, "/script/create_table_framework.sql");
            
            logger.info("Creating model database...");
            runSQLScript(con, "/script/create_table_model.sql");
            
            logger.info("Populating model database for Peer "+syncPeerMe.getPeerName());
            runSQLScript(con, "/script/populate_database_peer"+syncPeerMe.getPeerName()+".sql");
            
            con.commit();
            con.close();
            
        } catch (IOException | SQLException e) {
            logger.log(Level.SEVERE, "Cannot prepare database." + e);
            throw e;
        }
    }
    
    private void runSQLScript(Connection con, String path) throws IOException, SQLException {
        InputStream in = this.getClass().getResourceAsStream(path);
        Scanner sc = new Scanner(in);
        sc.useDelimiter(";");
        
        while(sc.hasNext()){
            String sql = sc.next();
            if(StringUtils.isWhiteSpaceOnly(sql)){
                break;
            }
            logger.info(sql);
            Statement stmt = con.createStatement();
            stmt.execute(sql);
            stmt.close();
        }
        sc.close();
        logger.info("Executed " + path + " successfully.");
    }
}
