package com.circlework;

import java.util.List;

public class Row {

    private final List<Object> map;

    public Row(List<Object> args){
        map = args;
    }

    public <T> T get(int index) {
        return (T) map.get(index);
    }

    @Override
    public String toString() {
        return "Row{" +
                map +
                '}';
    }
}