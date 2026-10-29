package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import model.annotation.MaxLength;
import model.annotation.Positive;

/** Practical 7: reads annotations at run time (reflection) and checks any object. */
public final class AnnotationValidator {
    private AnnotationValidator() {
    }

    /** Returns only the messages, e.g. {"must be > 0"}; empty array = valid. */
    public static String[] validate(Object obj) {
        return check(obj, false);
    }

    /** Supplementary: same, but each message starts with the field name. */
    public static String[] validateWithNames(Object obj) {
        return check(obj, true);
    }

    private static String[] check(Object obj, boolean withNames) {
        List<String> errors = new ArrayList<>();
        for (Field field : ReflectionUtil.allFields(obj.getClass())) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (field.isAnnotationPresent(Positive.class) && value instanceof Number n && n.doubleValue() <= 0) {
                    errors.add(prefix(field, withNames) + field.getAnnotation(Positive.class).message());
                }
                if (field.isAnnotationPresent(MaxLength.class) && value instanceof String s) {
                    int max = field.getAnnotation(MaxLength.class).value();
                    if (s.length() > max) {
                        errors.add(prefix(field, withNames) + "must be at most " + max + " characters");
                    }
                }
            } catch (IllegalAccessException e) {
                errors.add(prefix(field, withNames) + "cannot be read");
            }
        }
        return errors.toArray(new String[0]);
    }

    private static String prefix(Field field, boolean withNames) {
        return withNames ? field.getName() + ": " : "";
    }
}
