package utils;

import java.util.Map;
import java.util.HashMap;

/*
 * Small class to manage a simple cache.
 */
public class Cache {
    private final Map<String, Entry> hashmap;
    private final int size;
    private final float threshold;

    /*
     * Constructs an object Cache with a size and a threshold.
     * @param size : the maximal size of the cache
     * @param threshold : the maximal threshold of frequency
     * @return None
     */
    public Cache(int size, float threshold) {
        this.hashmap = new HashMap<>();
        this.size = size;
        this.threshold = threshold;
    }

    /*
     * Adds a new element in the cache.
     * @param request : the request linked to the new addition
     * @param response : the response corresponding to the request
     * @return None
     */
    public synchronized void add(String request, String response) {
        // If the cache is full
        if (hashmap.size() >= this.size) {
            String leastFreq = null;
            int minFreq = Integer.MAX_VALUE;

            // The least frequent element is removed
            int sumFreq = 0;
            for (Map.Entry<String, Entry> entry : hashmap.entrySet()) {
                sumFreq += entry.getValue().freq;
                if (entry.getValue().freq < minFreq) {
                    leastFreq = entry.getKey();
                }
            }
            hashmap.remove(leastFreq);

            // The frequencies are adjusted if the threshold is reached
            if (sumFreq/(float) hashmap.size() > this.threshold) {
                for (Entry entry : hashmap.values()) {
                    entry.freq /= 2;
                }
            }
        }
        hashmap.put(request, new Entry(response, 1));
    }

    /*
     * Gets the response corresponding to the request if it is in the cache, otherwise null.
     * @param request : the request searched
     * @return response : corresponding to the request or null if the request is not in the cache
     */
    public String get(String request) {
        if (hashmap.containsKey(request)) {
            Entry entry = hashmap.get(request);
            entry.freq += 1;
            return entry.response;
        } else {
            return null;
        }
    }

    /*
     * Internal class of the objects stored in the cache.
     */
    public static class Entry {
        public String response;
        public int freq;

        /*
         * Constructs an object Entry with a response and a frequency
         * @param response : the element to store
         * @param freq : the frequency of the request for this element
         * @return None
         */
        public Entry(String response, int freq) {
            this.response = response;
            this.freq = freq;
        }
    }
}