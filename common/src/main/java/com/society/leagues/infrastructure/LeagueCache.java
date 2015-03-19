package com.society.leagues.infrastructure;

import com.society.leagues.client.api.domain.LeagueObject;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class LeagueCache<T extends LeagueObject> {
    
    private Map<Integer,T> cache = new HashMap<>();

    public synchronized final Collection<T> get() {
        return cache.values();
    }
    
    public synchronized final T get(Integer id)  {
        List<T> objects = get(Arrays.asList(id));
        return objects.isEmpty() ? null : objects.get(0);
    }
        
    public synchronized final List<T> get(List<Integer> ids)  {
        final List<T> objects = new ArrayList<>();
        ids.forEach(i -> { 
            T obj = cache.getOrDefault(i,null);
            if (obj != null)
                objects.add(obj);
        } );
        
        return objects;
    }
    
    public synchronized void clear() {
        cache.clear();
    }

    public synchronized void set(Collection<T> objects) {
        if (objects == null)
            return;

        objects.stream().forEach(o -> cache.put(o.getId(),o));
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }
    
}
