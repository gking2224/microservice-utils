package me.gking2224.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// to allow autowiring of lists
public class ListWrapper<E> {

    private List<E> list;
    
    public ListWrapper(ArrayList<E> list) {
        this.list = list;
    }
    
    public ListWrapper(int size) {
        this(new ArrayList<E>());
    }
    
    public ListWrapper() {
        this(0);
    }
    
    public List<E> get() {
        return list;
    }

    public boolean add(E e) {
        return list.add(e);
    }

    public boolean remove(Object o) {
        return list.remove(o);
    }

    public boolean addAll(Collection<? extends E> c) {
        return list.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return list.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public void clear() {
        list.clear();
    }

    public E set(int index, E element) {
        return list.set(index, element);
    }

    public void add(int index, E element) {
        list.add(index, element);
    }

    public E remove(int index) {
        return list.remove(index);
    }
}
