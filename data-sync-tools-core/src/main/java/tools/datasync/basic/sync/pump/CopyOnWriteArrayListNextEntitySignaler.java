package tools.datasync.basic.sync.pump;

import java.util.concurrent.CopyOnWriteArrayList;

import tools.datasync.basic.util.StringUtils;

public class CopyOnWriteArrayListNextEntitySignaler implements
	NextEntitySignaler {

    private CopyOnWriteArrayList<String> arrayList;

    @Override
    public void tellPeerReadyForNextEntity(String previousEntityId) {
	arrayList.add(previousEntityId);
    }

    public String toString() {
	StringBuilder answer = new StringBuilder();
	answer.append(StringUtils.getSimpleName(this));
	answer.append("{");
	answer.append("arrayListHashCode=");
	answer.append(arrayList.hashCode());
	answer.append(",");
	answer.append("arrayList=");
	answer.append(arrayList.toString());
	answer.append("}");
	return (answer.toString());
    }
}
