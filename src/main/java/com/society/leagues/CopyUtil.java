package com.society.leagues;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CopyUtil {

    public static  <T> List<T> copy(Collection sources, Class<T> dest) {
        try {
            List<T> objects = new ArrayList<>();
            Constructor<T> constructor = dest.getDeclaredConstructor();

            for (Object source : sources) {
                T newDest  =  constructor.newInstance();
                ReflectionUtils.shallowCopyFieldState(source,newDest);
                objects.add(newDest);
            }
            return objects;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
