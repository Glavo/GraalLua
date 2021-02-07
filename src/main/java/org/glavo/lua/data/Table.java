package org.glavo.lua.data;

import java.util.*;

public final class Table implements Map<Object, Object> {
    private Object[] array;
    int arraySize = 0;

    private Map<Object, Object> table;

    @Override
    public final int size() {
        final Object[] array = this.array;
        final Map<Object, Object> table = this.table;
        int c = 0;
        if (array != null) {
            c += array.length;
        }
        if (table != null) {
            c += table.size();
        }
        return c;
    }

    @Override
    public final boolean isEmpty() {
        return (array == null || array.length == 0) && (table == null || table.isEmpty());
    }

    @Override
    public final boolean containsKey(Object key) {
        if (key instanceof Integer) {
            int kv = (Integer) key;
            if (kv > 0 && kv <= arraySize) {
                return true;
            }
        }

        final Map<Object, Object> table = this.table;
        return table != null && table.containsKey(key);
    }

    @Override
    public final boolean containsValue(Object value) {
        final int arraySize = this.arraySize;

        if (arraySize > 0) {
            final Object[] array = this.array;

            if (value == null) {
                for (int i = 0; i < arraySize; i++) {
                    if (null == array[i]) {
                        return true;
                    }
                }
            } else {
                for (int i = 0; i < arraySize; i++) {
                    if (value.equals(array[i])) {
                        return true;
                    }
                }
            }
        }
        final Map<Object, Object> table = this.table;
        return table != null && table.containsValue(value);
    }

    @Override
    public final Object get(Object key) {
        final Object[] array = this.array;
        if (array != null) {
            if (key instanceof Integer) {
                int kv = (Integer) key;
                if (kv > 0 && kv <= array.length) {
                    return array[kv - 1];
                }
            }
        }
        final Map<Object, Object> table = this.table;
        return table.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void putAll(Map<?, ?> m) {
        //noinspection ResultOfMethodCallIgnored
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        arraySize = 0;
        table = null;
        array = null;
    }

    @Override
    public Set<Object> keySet() {
        final int arraySize = this.arraySize;
        final Map<Object, Object> table = this.table;
        if (arraySize == 0) {
            return table == null ? Collections.emptySet() : table.keySet();
        } else {
            HashSet<Object> set = new HashSet<>();
            for (int i = 1; i <= arraySize; i++) {
                set.add(i);
            }
            if (table != null) {
                set.addAll(table.keySet());
            }
            return set;
        }
    }

    @Override
    public final Collection<Object> values() {
        final int arraySize = this.arraySize;
        final Map<Object, Object> table = this.table;

        if (arraySize == 0) {
            return table == null ? Collections.emptyList() : table.values();
        } else {
            final Object[] array = this.array;
            if (table == null) {
                return Arrays.asList(array).subList(0, arraySize);
            } else {
                ArrayList<Object> list = new ArrayList<>();
                list.addAll(Arrays.asList(array).subList(0, arraySize));
                list.addAll(table.values());
                return list;
            }
        }
    }

    @Override
    public final Set<Entry<Object, Object>> entrySet() {
        final int arraySize = this.arraySize;
        final Map<Object, Object> table = this.table;
        if (arraySize == 0) {
            return table == null ? Collections.emptySet() : table.entrySet();
        } else {
            final Object[] array = this.array;
            HashMap<Object, Object> map = new HashMap<>();
            for (int i = 0; i < arraySize; i++) {
                map.put(i + 1, array[i]);
            }
            if (table != null) {
                map.putAll(table);
            }
            return map.entrySet();
        }
    }
}
