/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.model.generator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomGenerator {

    protected static final Logger LOG = LoggerFactory.getLogger(RandomGenerator.class);

    public static <T> T randomObject(Class<T> objectClass) {
        return randomObject(objectClass, null);
    }
    
    public static <T, V> T randomObject(Class<T> objectClass, Class<V> typeClass1) {
        return randomObject(objectClass, typeClass1, null);
    }
    
    public static <T, V, W> T randomObject(Class<T> objectClass, Class<V> typeClass1, Class<W> typeClass2) {
        T object = null;
            
        try {
            if (boolean.class.isAssignableFrom(objectClass) ||
                Boolean.class.isAssignableFrom(objectClass))  {
                object = (T) randomBoolean();
            } else if (byte.class.isAssignableFrom(objectClass) ||
                Byte.class.isAssignableFrom(objectClass))  {
                object = (T) randomByte();
            } else if (char.class.isAssignableFrom(objectClass) ||
                Character.class.isAssignableFrom(objectClass))  {
                object = (T) randomChar();
            } else if (short.class.isAssignableFrom(objectClass) ||
                Short.class.isAssignableFrom(objectClass))  {
                object = (T) randomShort();
            } else if (int.class.isAssignableFrom(objectClass) ||
                Integer.class.isAssignableFrom(objectClass))  {
                object = (T) randomInt();
            } else if (long.class.isAssignableFrom(objectClass) ||
                Long.class.isAssignableFrom(objectClass))  {
                object = (T) randomLong();
            } else if (float.class.isAssignableFrom(objectClass) ||
                Float.class.isAssignableFrom(objectClass))  {
                object = (T) randomFloat();
            } else if (double.class.isAssignableFrom(objectClass) ||
                Double.class.isAssignableFrom(objectClass))  {
                object = (T) randomDouble();
            } else if (String.class.isAssignableFrom(objectClass)) {
                object = (T) randomString();
            } else if (Date.class.isAssignableFrom(objectClass)) {
                object = (T) randomDate();
            } else if (Map.class.isAssignableFrom(objectClass)) {
                object = (T) randomMap((Class<Map>) objectClass, typeClass1, typeClass2);
            } else if (List.class.isAssignableFrom(objectClass)) {
                object = (T) randomList((Class<List>) objectClass, typeClass1);
            } else if (Set.class.isAssignableFrom(objectClass)) {
                object = (T) randomSet((Class<Set>) objectClass, typeClass1);
            } else if (Collection.class.isAssignableFrom(objectClass)) {
                object = (T) randomCollection((Class<Collection>) objectClass, typeClass1);
            } else if (objectClass.isArray()) {
                object = (T) randomArray(objectClass.getComponentType());
            } else if (objectClass.isEnum()) {
                object = (T) randomEnum(objectClass);
            } else {
                object = objectClass.newInstance();
            
                for (Field field : objectClass.getDeclaredFields()) {
                    if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        Class fieldType = field.getType();

                        field.setAccessible(true);

                        if (field.getGenericType() instanceof ParameterizedType) {
                            ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                            Type[] typeArgs = paramType.getActualTypeArguments();

                            if (typeArgs.length == 1) {
                                if (typeArgs[0] instanceof ParameterizedType) {
                                    // TODO: HANDLE NESTED PARAM TYPE
                                } else {
                                    field.set(object, randomObject(fieldType, (Class<?>) typeArgs[0]));
                                }
                            } else if (typeArgs.length == 2) {
                                field.set(object, randomObject(fieldType, (Class<?>) typeArgs[0], (Class<?>) typeArgs[1]));
                            }
                        } else {
                            field.set(object, randomObject(fieldType));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            //LOG.error("Exception while building the random object", ex);
        }
        
        return object;
    }
    
    public static <T> T randomEnum(Class<T> enumClass) {
        T[] enums = enumClass.getEnumConstants();
        return enums[RandomUtils.nextInt(enums.length)];
    }
    
    public static <T> T[] randomArray(Class<T> arrayClass) {
        List<T> arrayList = new ArrayList<T>();
        for (int i = 0; i <= RandomUtils.nextInt(3); i++) {
            arrayList.add(randomObject(arrayClass));
        }
        
        return (T[]) arrayList.toArray();
    }
    
    public static <T extends Map<V, W>, V, W> T randomMap(Class<T> setClass, Class<V> keyClass, Class<W> valueClass) {
        T mapObject = null;
        try {
            mapObject = setClass.newInstance();
            
            for (int i = 0; i <= RandomUtils.nextInt(3); i++) {
                mapObject.put(randomObject(keyClass), randomObject(valueClass));
            }
        } catch (Exception ex) {
            //LOG.error("Exception while building the random map", ex);
        }
        return mapObject;
    }
    
    public static <T extends Set<V>, V> T randomSet(Class<T> setClass, Class<V> valueClass) {
        T setObject = null;
        try {
            setObject = setClass.newInstance();
            
            for (int i = 0; i <= RandomUtils.nextInt(3); i++) {
                setObject.add(randomObject(valueClass));
            }
        } catch (Exception ex) {
            //LOG.error("Exception while building the random set", ex);
        }
        return setObject;
    }
    
    public static <T extends List<V>, V> T randomList(Class<T> listClass, Class<V> valueClass) {
        T listObject = null;
        try {
            listObject = listClass.newInstance();
            for (int i = 0; i <= RandomUtils.nextInt(3); i++) {
                listObject.add(randomObject(valueClass));
            }
        } catch (Exception ex) {
            //LOG.error("Exception while building the random list", ex);
        }
        return listObject;
    }
    
    public static <T extends Collection<V>, V> T randomCollection(Class<T> listClass, Class<V> valueClass) {
        T collectionObject = null;
        try {
            collectionObject = listClass.newInstance();
            for (int i = 0; i <= RandomUtils.nextInt(3); i++) {
                collectionObject.add(randomObject(valueClass));
            }
        } catch (Exception ex) {
            //LOG.error("Exception while building the random collection", ex);
        }
        return collectionObject;
    }
    
    public static Boolean randomBoolean() {
        return RandomUtils.nextBoolean();
    }
    
    public static Byte randomByte() {
        return (byte) (Byte.MIN_VALUE + RandomUtils.nextInt(Byte.MAX_VALUE - Byte.MIN_VALUE));
    }
    
    public static Character randomChar() {
        return (char) (Character.MIN_VALUE + RandomUtils.nextInt(Character.MAX_VALUE - Character.MIN_VALUE));
    }
    
    public static Short randomShort() {
        return (short) (Short.MIN_VALUE + RandomUtils.nextInt(Short.MAX_VALUE - Short.MIN_VALUE));
    }
    
    public static Integer randomInt() {
        return RandomUtils.nextInt();
    }
    
    public static Long randomLong() {
        return RandomUtils.nextLong();
    }
    
    public static Float randomFloat() {
        return RandomUtils.nextFloat();
    }
    
    public static Double randomDouble() {
        return RandomUtils.nextDouble();
    }
    
    public static String randomString() {
        return RandomStringUtils.randomAlphanumeric(32);
    }
    
    public static Date randomDate() {
        return new Date();
    }
}
