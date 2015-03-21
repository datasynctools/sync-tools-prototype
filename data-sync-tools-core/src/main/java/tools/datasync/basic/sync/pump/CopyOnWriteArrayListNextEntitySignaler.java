package tools.datasync.basic.sync.pump;

import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteArrayListNextEntitySignaler implements
	NextEntitySignaler {

    private CopyOnWriteArrayList<String> arrayList;

    @Override
    public void tellPeerReadyForNextEntity(String previousEntityId) {
	arrayList.add(previousEntityId);
    }

}
