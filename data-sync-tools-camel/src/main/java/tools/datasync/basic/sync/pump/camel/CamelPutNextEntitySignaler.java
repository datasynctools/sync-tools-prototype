package tools.datasync.basic.sync.pump.camel;

import static tools.datasync.api.msg.SyncMessageType.PEER_READY_WITH_NEXT_ENTITY;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.api.utils.Stringify;
import tools.datasync.dataformats.json.Jsonify;
import tools.datasync.model.EnityId;
import tools.datasync.pump.NextEntitySignaler;
import tools.datasync.utils.StringUtils;

public class CamelPutNextEntitySignaler implements NextEntitySignaler {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CamelPutNextEntitySignaler.class);

    private String updateUri;
    private ProducerTemplate template;

    private Stringify stringify = new Jsonify();

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
	syncMessage.setMessageType(PEER_READY_WITH_NEXT_ENTITY);
	syncMessage.setTimestamp(System.currentTimeMillis());

	String payload = stringify.toString(syncMessage);

	LOG.info("Sending that next peer is this component is ready for next "
		+ "entity now that entityId {} complete", previousEntityId);
	template.sendBody(updateUri, payload);

    }

    public void setStringify(Stringify stringify) {
	this.stringify = stringify;
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	answer.append("updateUri=");
	answer.append(updateUri);
	answer.append("}");
	return (answer.toString());
    }

}
