package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** Collects the fields of a class AND its superclasses (Account's fields live in the parent class). */
public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static List<Field> allFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                fields.add(f);
            }
        }
        return fields;
    }
}
