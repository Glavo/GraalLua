package org.glavo.lua.runtime;

import com.oracle.truffle.api.interop.TruffleObject;

import java.util.*;

public class LuaTable implements TruffleObject {
    private static final int DEFAULT_CAPACITY = 16;

    private Object[] array;
    int arraySize = 0;

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

    public final void set(Object key, Object newValue) {
        if (key == null || key == LuaNil.Nil) {
            throw new NullPointerException();
        }
        setImpl(key, newValue == LuaNil.Nil ? null : newValue);
    }

    final void setImpl(/* NotNull */ Object key, Object newValue) {
        if (key instanceof Integer) {
            final int kv = (Integer) key;
            if (kv > 0) {
                final int as = this.arraySize;
                if (kv <= as) {
                    array[kv] = newValue;
                    return;
                } else if (kv == as + 1) {
                    if (newValue == null) {
                        return;
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
                    arr[as] = newValue;
                    ++this.arraySize;
                    return;
                }
            }
        }
        if (newValue == null) {
            table.remove(key);
        } else {
            table.put(key, newValue);
        }
    }

    public final Map<Object, Object> asJavaMap() {
        throw new UnsupportedOperationException(); // TODO
    }
}
