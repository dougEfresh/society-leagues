package com.society.leagues.dao;

import com.rits.cloning.Cloner;
import com.society.leagues.client.api.domain.LeagueObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.*;


public class LeagueCache<T extends LeagueObject> {
    final Cloner cloner;

    public LeagueCache(Cloner cloner) {
        this.cloner = cloner;
    }

    private Map<Integer,T> cache = new HashMap<>();

    public synchronized void modify(Integer id, T thing) {
        cache.put(id,thing);
    }

    public synchronized void remove(T thing) {
        cache.remove(thing);
    }

    public synchronized void add(T thing) {
        cache.put(thing.getId(),thing);
    }
    public synchronized final Collection<T> get() {
        //return cloner.deepClone(cache.values());
        return cache.values();
    }
    
    public synchronized final T get(Integer id)  {
        List<T> objects = get(Arrays.asList(id));
        return objects.isEmpty() ? null : objects.get(0);
        //return objects.isEmpty() ? null : cloner.deepClone(objects.get(0));
    }
        
    public synchronized final List<T> get(List<Integer> ids)  {
        final List<T> objects = new ArrayList<>();
        ids.forEach(i -> { 
            T obj = cache.getOrDefault(i,null);
            if (obj != null)
                objects.add(obj);
        } );

        return objects;
        //return cloner.deepClone(objects);
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
