package com.xtr.framework.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSliceUtil {

    /**
     * 将一个List按照指定n个对象为一组顺序切分
     * @param source
     * @param n
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> sequenceAssign(List<T> source, int n) {
        List<List<T>> partition = null;
        try {
            partition = partition(source, n);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return partition;
    }

    public static <T> List<List<T>> partition(final List<T> list, final int size) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        return new Partition<>(list, size);
    }

    private static class Partition<T> extends AbstractList<List<T>> {
        private final List<T> list;
        private final int size;

        private Partition(final List<T> list, final int size) {
            this.list = list;
            this.size = size;
        }

        @Override
        public List<T> get(final int index) {
            final int listSize = size();
            if (index < 0) {
                throw new IndexOutOfBoundsException("Index " + index + " must not be negative");
            }
            if (index >= listSize) {
                throw new IndexOutOfBoundsException("Index " + index + " must be less than size " +
                        listSize);
            }
            final int start = index * size;
            final int end = Math.min(start + size, list.size());
            return list.subList(start, end);
        }

        @Override
        public int size() {
            return (int) Math.ceil((double) list.size() / (double) size);
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
    }




    /**
     * 将一个List等分成n个list
     * @param source
     * @param n
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        //(先计算出余数)
        int remainder = source.size() % n;
        //然后是商
        int number = source.size() / n;
        //偏移量
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<List<Integer>> test = sequenceAssign(list, 4);
        for (List<Integer> integers : test) {
            System.out.println(integers.toString());
        }

        /*List<List<Integer>> lists = averageAssign(list, 4);
        for (List<Integer> integers : lists) {
            System.out.println(integers.toString());
        }*/
    }

}

