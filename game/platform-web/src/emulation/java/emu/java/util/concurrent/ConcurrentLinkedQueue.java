package emu.java.util.concurrent;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;

public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> {

    private final ArrayDeque<E> elements = new ArrayDeque<>();

    public ConcurrentLinkedQueue() {
    }

    public ConcurrentLinkedQueue(Collection<? extends E> initial) {
        elements.addAll(initial);
    }

    @Override
    public boolean offer(E element) {
        return elements.offer(element);
    }

    @Override
    public E poll() {
        return elements.poll();
    }

    @Override
    public E peek() {
        return elements.peek();
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }
}
