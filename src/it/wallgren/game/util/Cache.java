package it.wallgren.game.util;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache<KEY, TYPE> {
	private HashMap<KEY, Entry> hash;
	private int limit;

	private Entry first;

	private Entry last;

	private ReadWriteLock lock = new ReentrantReadWriteLock();

	public Cache(int limit) {
		super();
		this.limit = limit;
		hash = new HashMap<KEY, Entry>(limit * 3);
	}

	public void put(KEY key, TYPE value) {
		Entry entry = new Entry(key, value);
		lock.writeLock().lock();
		try {
			// Link the new entry
			link(entry);

			// put the new value to hash, entry is set to any old value on this
			// key
			entry = hash.put(key, entry);

			// unlink any old items that might be stored on this key
			if (entry != null) {
				unlink(entry);
			}
			while (hash.size() > limit) {
				hash.remove(last.key);
				unlink(last);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public TYPE get(KEY key) {
		Entry entry;
		lock.readLock().lock();
		try {
			entry = hash.get(key);

		} finally {
			lock.readLock().unlock();
		}

		if (entry != null) {
			lock.writeLock().lock();
			try {
				relink(entry);
				return entry.object;
			} finally {
				lock.writeLock().unlock();
			}
		} else {
			return null;
		}
	}

	public void delete(KEY key) {
		Entry entry;
		lock.readLock().lock();
		try {
			entry = hash.get(key);
		} finally {
			lock.readLock().unlock();
		}
		if (entry == null) {
			return;
		}
		lock.writeLock().lock();
		try {
			unlink(entry);
			hash.remove(key);
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void link(Entry entry) {
		if (first == null) {
			first = entry;
			last = first;
		} else {
			entry.next = first;
			first.prev = entry;
			first = entry;
		}
	}

	private void unlink(Entry entry) {
		if (entry.prev != null) {
			entry.prev.next = entry.next;
		} else {
			// if prev == null entry is the first value
			first = entry.next;
		}

		if (entry.next != null) {
			entry.next.prev = entry.prev;
		} else {
			// if next == null entry is the last value
			last = entry.prev;
		}
		entry.prev = null;
		entry.next = null;
	}

	private void relink(Entry entry) {
		unlink(entry);
		link(entry);
	}

	private class Entry {
		KEY key;
		TYPE object;
		Entry prev;
		Entry next;

		public Entry(KEY key, TYPE object) {
			super();
			this.key = key;
			this.object = object;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Cache.Entry) {
				Cache.Entry entry = (Cache.Entry) obj;
				return key.equals(entry.key);
			}
			return false;
		}

		@Override
		public int hashCode() {
			if (key == null) {
				return super.hashCode();
			}
			return key.hashCode();
		}
	}

	public int getSize() {
		return hash.size();
	}
}
