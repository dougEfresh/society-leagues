package com.society.leagues.cache;

import com.society.leagues.client.api.domain.*;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class CachedCollection<T extends List<LeagueObject>> implements Comparable<CachedCollection> {
    final AtomicReference<T> entity = new AtomicReference(new ArrayList<>());
    final AtomicReference<Set<LeagueObject>> current =  new AtomicReference<>(new HashSet<>());
    private static Logger logger = Logger.getLogger(CachedCollection.class);
    final MongoRepository repo;
    final String collectionName;
    Integer order = Integer.MAX_VALUE;

    public CachedCollection(MongoRepository repo) {
        this.repo = repo;
        String name = repo.getClass().getInterfaces()[0].getSimpleName().replace("Repository","");
        this.collectionName = name.substring(0,1).toLowerCase() + name.substring(1,name.length());
        setOrder();
    }

    private void setOrder() {
        if (collectionName.equals("season")) {
            order = 0;
            return;
        }

        if (collectionName.equals("user")) {
            order = 1;
            return;
        }

        if (collectionName.equals("team")) {
            order = 2;
            return;
        }

        if (collectionName.equals("teamMatch")) {
            order = 3;
            return;
        }

        if (collectionName.equals("playerResult")) {
            order = 4;
            return;
        }

    }

    public T get() {
        return entity.get();
    }


    public Set<LeagueObject> current() {
        return current.get();
    }

    public void add(LeagueObject obj) {
        if (isCurrent(obj)) {
            current.get().add(obj);
        }
        entity.get().add(obj);
    }

    public void set(T collection) {
        this.current.lazySet(collection.stream().parallel().filter(this::isCurrent).collect(Collectors.toSet()));
        this.entity.lazySet(collection);
    }

    public MongoRepository getRepo() {
        return repo;
    }

    public String getCollection() {
        return collectionName;
    }

    @SuppressWarnings("unchecked")
    public void refresh() {
        logger.info("Refreshing " + collectionName);
        this.set((T) this.repo.findAll());
        logger.info("Done " + collectionName);
    }

    @Override
    public int compareTo(CachedCollection o) {
        return this.order.compareTo(o.order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CachedCollection<?> that = (CachedCollection<?>) o;

        return collectionName.equals(that.collectionName);

    }

    @Override
    public int hashCode() {
        return collectionName.hashCode();
    }

    private boolean isCurrent(LeagueObject entity) {
        if (entity instanceof Team)  {
            return  ((Team) entity).getSeason().isActive();
        }

        if (entity instanceof TeamMatch)  {
            return  ((TeamMatch) entity).getSeason().isActive();
        }

        if (entity instanceof PlayerResult)  {
            return  ((PlayerResult) entity).getSeason().isActive();
        }

        if (entity instanceof Season)  {
            return  ((Season) entity).isActive();
        }

        if (entity instanceof User)  {
            User u  = ((User) entity);
            return u.getHandicapSeasons().stream().filter(hs->hs.getSeason().isActive()).count() > 0;
        }
        return false;
    }
}
