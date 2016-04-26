package com.github.tototoshi.dbcache;

public class NotSerializableClass {

    private int i;

    public NotSerializableClass(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotSerializableClass that = (NotSerializableClass) o;

        return i == that.i;

    }

    @Override
    public int hashCode() {
        return i;
    }
}
