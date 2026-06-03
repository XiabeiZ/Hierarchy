## Code Review

You are reviewing the following code submitted as part of a task to implement an item cache in a highly concurrent application. The anticipated load includes: thousands of reads per second, hundreds of writes per second, tens of concurrent threads.
Your objective is to identify and explain the issues in the implementation that must be addressed before deploying the code to production. Please provide a clear explanation of each issue and its potential impact on production behaviour.

```java
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    // private final long ttlNanos = TimeUnit.MINUTES.toNanos(1); Please see comments below
    private final long ttlMs = 60000; // 1 minute
    // Scheduled cleanup
    //private final ScheduledExecutorService evictionScheduler;
        

    
      //public SimpleCache(long ttlMs) {
        //this.ttlMs = ttlMs;

        // background cleanup thread
        //this.cleaner = Executors.newSingleThreadScheduledExecutor();
        //this.cleaner.scheduleAtFixedRate(this::cleanup, ttlMs, ttlMs, TimeUnit.MILLISECONDS);
    //  }
    
    public static class CacheEntry<V> {
        private final V value;
        private final long timestamp;
        
        public CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public V getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    public void put(K key, V value) {
        // Switch from System.currentTimeMillis() to System.nanoTime(). System.nanoTime() won't be affected if the system time is changed.
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            // Switch from System.currentTimeMillis() to System.nanoTime(). System.nanoTime() won't be affected if the system time is changed.
            if (System.currentTimeMillis() - entry.getTimestamp() < ttlMs) {
                return entry.getValue();
            }
            //The key is not removed after the timeout. 
            //Add 
            //else {
            //    cache.remove(key, entry);
            //    return null;
            //}
        }
        return null;
    }

    public int size() {
    //It's returning the size of the raw cache including expired entries.
        return cache.size();
    }
    
    //private void cleanup() {
    //    long now = System.nanoTime();
    //    cache.entrySet().removeIf(e -> e.getValue().expireAt < now);
    //}
}
```
Comment:
1. The cache may get a large amount of keys and most of them may be expired but is not checked until the get(key) method is called, and this method does not remove the expired key-entry either. Suggestions: 1) in get(key) method, once it finds that the key-entry is expired, remove it from the cache; 2) Run a scheduled clean up service to remove expired entries.
2. The code is using System.currentTimeMillis() instead of System.nanoTime(). System.currentTimeMillis() is wall-clock time and is subject to system clock changes (NTP, manual adjustment), which can break TTL semantics. A monotonic clock like System.nanoTime() should be used for duration measurement.
3. If a hot key is expired and multiple threads try to access the same key, it returns a null, and multiple concurrent requests trigger redundant backend calls, potentially overwhelming downstream services. Instead, limit one thread in doing this while other thread waits for the result. Suggestion: Use ConcurrentHashMap.compute().
4. size() is not accurate. It returns the size of the raw cache including expired entries.