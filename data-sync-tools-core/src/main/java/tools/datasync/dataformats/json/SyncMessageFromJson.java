package tools.datasync.dataformats.json;

import org.codehaus.jackson.map.ObjectMapper;

import tools.datasync.api.msg.SyncMessage;
import tools.datasync.api.msg.SyncMessageFromT;
import tools.datasync.utils.ObjectMapperFactory;

public class SyncMessageFromJson implements SyncMessageFromT<String> {

    private ObjectMapper jsonMapper = ObjectMapperFactory.getInstance();

    @Override
    public SyncMessage create(String item) {
	SyncMessage syncMessage;
	try {
	    syncMessage = jsonMapper.readValue(item, SyncMessage.class);
	} catch (Exception e) {
	    throw (new RuntimeException(e));
	}

	return syncMessage;
    }

}
