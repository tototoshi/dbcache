package com.github.tototoshi.dbcache;

import java.io.Serializable;

public class SerializableClass implements Serializable {

    private int i;

    public SerializableClass(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializableClass that = (SerializableClass) o;

        return i == that.i;

    }

    @Override
    public int hashCode() {
        return i;
    }
}
