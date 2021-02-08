package org.glavo.lua.runtime;

import com.oracle.truffle.api.interop.TruffleObject;

import java.util.*;

public class LuaTable implements TruffleObject {
    private static final int DEFAULT_CAPACITY = 16;

    private Object[] array;
    int arraySize = 0;
    int arrayElementsCount = 0;

    private HashMap<Object, Object> table;

    //region Array Helpers

    private static Object[] growArray(int oldCapacity) {
        if (oldCapacity == 0) {
            return new Object[DEFAULT_CAPACITY];
        }
        final int minCapacity = oldCapacity + 1;
        int newCapacity = Math.max(Math.max(oldCapacity, minCapacity), oldCapacity + (oldCapacity >> 1));
        return new Object[newCapacity];
    }

    //endregion

    public final Object get(Object key) {
        if (key == null || key == LuaNil.Nil) {
            return LuaNil.Nil;
        }
        Object res = getImpl(key);
        return res == null ? LuaNil.Nil : res;
    }

    final Object getImpl(Object key) {
        if (key instanceof Integer) {
            final int arraySize = this.arraySize;
            if (arraySize > 0) {
                final Object[] array = this.array;
                int kv = (Integer) key;
                if (kv > 0 && kv <= arraySize) {
                    return array[kv - 1];
                }
            }
        }
        final HashMap<Object, Object> table = this.table;
        return table == null ? null : table.get(key);
    }

    public final Object put(Object key, Object newValue) {
        if (key == null || key == LuaNil.Nil) {
            throw new NullPointerException();
        }
        return putImpl(key, newValue == LuaNil.Nil ? null : newValue);
    }

    final Object putImpl(/* NotNull */ Object key, Object newValue) {
        HashMap<Object, Object> table = this.table;
        if (key instanceof Integer) {
            final int kv = (Integer) key;
            if (kv > 0) {
                final int as = this.arraySize;
                final int idx = kv - 1;
                if (kv <= as) {
                    final Object[] array = this.array;
                    Object oldValue = array[idx];
                    if (newValue == null && oldValue != null) {
                        --this.arrayElementsCount;
                    } else if (newValue != null && oldValue == null) {
                        ++this.arrayElementsCount;
                    }
                    array[idx] = newValue;
                    return oldValue;
                } else if (idx == as) {
                    if (newValue == null) {
                        return table == null ? null : table.remove(key);
                    }
                    Object[] arr = this.array;
                    if (arr == null) {
                        assert as == 0;
                        arr = new Object[DEFAULT_CAPACITY];
                        this.array = arr;
                    } else if (arr.length == as) {
                        Object[] newArr = growArray(as);
                        System.arraycopy(arr, 0, newArr, 0, as);
                        this.array = arr = newArr;
                    }
                    arr[idx] = newValue;
                    ++this.arraySize;
                    ++this.arrayElementsCount;
                    return table == null ? null : table.remove(key);
                }
            }
        }
        if (newValue == null) {
            return table == null ? null : table.remove(key);
        } else {
            if (table == null) {
                this.table = (table = new HashMap<>());
            }
            return table.put(key, newValue);
        }
    }

    public final Map<Object, Object> asJavaMap() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public String toString() {
        //TODO
        return "LuaTable{" +
                "array=" + Arrays.toString(array) +
                ", arraySize=" + arraySize +
                ", arrayElementsCount=" + arrayElementsCount +
                ", table=" + table +
                '}';
    }
}
