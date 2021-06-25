package net.jacobpeterson.alpaca.util.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * {@link ReflectionUtil} is a utility class for Java Reflection.
 */
public class ReflectionUtil {

    private static final HashMap<Class<?>, ArrayList<Field>> ALL_DECLARED_FIELDS_CACHE = new HashMap<>();

    /**
     * Gets all declared {@link Field}s in the inheritance tree. This method is very inefficient so caching of results
     * has been implemented.
     *
     * @param clazz the {@link Class}
     *
     * @return all the declared {@link Field}s
     */
    public static ArrayList<Field> getAllDeclaredFields(Class<?> clazz) {
        // Check cache
        ArrayList<Field> cachedFields = ALL_DECLARED_FIELDS_CACHE.get(clazz);
        if (cachedFields != null) {
            return cachedFields;
        }

        ArrayList<Field> declaredFields = new ArrayList<>();

        // Loop while currentClass has a superclass and add declared fields
        Class<?> currentClass = clazz;
        do {
            for (Field currentDeclaredField : currentClass.getDeclaredFields()) {
                // Check if the field name already exists in the list
                if (declaredFields.stream()
                        .map(Field::getName)
                        .noneMatch(fieldName -> fieldName.equals(currentDeclaredField.getName()))) {
                    declaredFields.add(currentDeclaredField);
                }
            }

            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);

        ALL_DECLARED_FIELDS_CACHE.put(clazz, declaredFields);

        return declaredFields;
    }

    /**
     * Gets a {@link Field}s in the inheritance tree.
     *
     * @param clazz     the {@link Class}
     * @param fieldName the {@link Field} name
     *
     * @return the declared {@link Field} (null if not found)
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        for (Field field : getAllDeclaredFields(clazz)) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        return null;
    }
}
