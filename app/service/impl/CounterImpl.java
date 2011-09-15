package service.impl;

import play.cache.Cache;
import service.api.Counter;

import org.springframework.stereotype.Service;

@Service("counter")
public class CounterImpl implements Counter {
    private static final long initial = 10000000L; // (ten million)
    private static final String COUNTER_KEY = "play:counter";

    public long next() {
        Long current = (Long) Cache.get(COUNTER_KEY);
        if (current == null) {
            current = initial;
        } else {
            current++;
        }
        Cache.set(COUNTER_KEY, current);

        return current;
    }
}
