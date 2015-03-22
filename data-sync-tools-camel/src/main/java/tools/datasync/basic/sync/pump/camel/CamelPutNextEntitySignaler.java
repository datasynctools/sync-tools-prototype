package tools.datasync.basic.sync.pump.camel;

import org.apache.camel.ProducerTemplate;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.basic.comm.SyncMessage;
import tools.datasync.basic.comm.SyncMessageType;
import tools.datasync.basic.model.EnityId;
import tools.datasync.basic.sync.pump.NextEntitySignaler;
import tools.datasync.basic.util.ObjectMapperFactory;

public class CamelPutNextEntitySignaler implements NextEntitySignaler {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelPutNextEntitySignaler.class);

    private String updateUri;
    private ProducerTemplate template;

    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();

    public CamelPutNextEntitySignaler(String updateUri,
	    ProducerTemplate template) {
	this.updateUri = updateUri;
	this.template = template;
    }

    public void tellPeerReadyForNextEntity(String previousEntityId) {

	SyncMessage syncMessage = new SyncMessage();
	// TODO: ADD Message Number capability to
	syncMessage.setMessageNumber(-1);
	EnityId entityIdObj = new EnityId();
	entityIdObj.setEntityId(previousEntityId);
	syncMessage.setPayloadData(entityIdObj);
	syncMessage.setMessageType(SyncMessageType.PEER_READY_WITH_NEXT_ENTITY
		.toString());
	syncMessage.setTimestamp(System.currentTimeMillis());

	String payload;
	try {
	    payload = jsonMapper.writeValueAsString(syncMessage);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}

	LOG.info("Sending body {}", payload);
	template.sendBody(updateUri, payload);

    }

}
