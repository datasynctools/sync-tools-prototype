package tools.datasync.basic.sync.pump.camel;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPartialBlockingQueue implements
	BlockingQueue<String> {

    public String remove() {
	throw (new RuntimeException("Not Implemented"));
    }

    public String poll() {
	throw (new RuntimeException("Not Implemented"));
    }

    public String element() {
	throw (new RuntimeException("Not Implemented"));
    }

    public String peek() {
	throw (new RuntimeException("Not Implemented"));
    }

    public int size() {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean isEmpty() {
	throw (new RuntimeException("Not Implemented"));
    }

    public Iterator<String> iterator() {
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

    public boolean addAll(Collection<? extends String> c) {
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

    public boolean add(String e) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean offer(String e) {
	throw (new RuntimeException("Not Implemented"));
    }

    public boolean offer(String e, long timeout, TimeUnit unit)
	    throws InterruptedException {
	throw (new RuntimeException("Not Implemented"));
    }

    public String take() throws InterruptedException {
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

    public int drainTo(Collection<? super String> c) {
	throw (new RuntimeException("Not Implemented"));
    }

    public int drainTo(Collection<? super String> c, int maxElements) {
	throw (new RuntimeException("Not Implemented"));
    }

}
