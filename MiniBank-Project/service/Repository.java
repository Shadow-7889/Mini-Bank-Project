package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import model.annotation.Id;
import util.ReflectionUtil;

/** Practical 13: one generic repository for any type that has an @Id field. */
public class Repository<T> {
    private final HashMap<String, T> items = new HashMap<>();

    public T save(T entity) {
        items.put(idOf(entity), entity);
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... entities) {
        for (T entity : entities) {
            save(entity);
        }
    }

    public T findById(String id) {
        return items.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(items.values());
    }

    public boolean delete(String id) {
        return items.remove(id) != null;
    }

    public int count() {
        return items.size();
    }

    /** Finds the field marked @Id by reflection and reads its value as the key. */
    private String idOf(T entity) {
        for (Field field : ReflectionUtil.allFields(entity.getClass())) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return String.valueOf(field.get(entity));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Cannot read @Id field " + field.getName(), e);
                }
            }
        }
        throw new IllegalArgumentException("No @Id field found in " + entity.getClass().getSimpleName());
    }

    /** Bounded type parameter: only Integer, Double, Long ... (subclasses of Number) are accepted. */
    public static <N extends Number> double average(N[] values) {
        if (values.length == 0) {
            return 0;
        }
        double sum = 0;
        for (N value : values) {
            sum += value.doubleValue();
        }
        return sum / values.length;
    }
}
