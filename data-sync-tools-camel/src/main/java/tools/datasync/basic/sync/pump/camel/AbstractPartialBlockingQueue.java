package tools.datasync.basic.sync.pump.camel;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import tools.datasync.basic.comm.SyncMessage;

public abstract class AbstractPartialBlockingQueue implements
	BlockingQueue<SyncMessage> {

    public SyncMessage remove() {
	throw (new RuntimeException("Not Implemented"));
    }

    public SyncMessage poll() {
	throw (new RuntimeException("Not Implemented"));
    }

    public SyncMessage element() {
	throw (new RuntimeException("Not Implemented"));
    }

    public SyncMessage peek() {
	throw (new RuntimeException("Not Implemented"));
    }

    public int size() {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean isEmpty() {
	throw (new RuntimeException("Not Implemented"));
    }

    public Iterator<SyncMessage> iterator() {
	throw (new RuntimeException("Not Implemented"));
    }

    public Object[] toArray() {
	throw (new RuntimeException("Not Implemented"));
    }

    public <T> T[] toArray(T[] a) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean containsAll(Collection<?> c) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean addAll(Collection<? extends SyncMessage> c) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean removeAll(Collection<?> c) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean retainAll(Collection<?> c) {
	throw (new RuntimeException("Not Implemented"));
    }

    public void clear() {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean add(SyncMessage e) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean offer(SyncMessage e) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean offer(SyncMessage e, long timeout, TimeUnit unit)
	    throws InterruptedException {
	throw (new RuntimeException("Not Implemented"));
    }

    public SyncMessage take() throws InterruptedException {
	throw (new RuntimeException("Not Implemented"));
    }

    public int remainingCapacity() {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean remove(Object o) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean contains(Object o) {
	throw (new RuntimeException("Not Implemented"));
    }

    public int drainTo(Collection<? super SyncMessage> c) {
	throw (new RuntimeException("Not Implemented"));
    }

    public int drainTo(Collection<? super SyncMessage> c, int maxElements) {
	throw (new RuntimeException("Not Implemented"));
    }

}
