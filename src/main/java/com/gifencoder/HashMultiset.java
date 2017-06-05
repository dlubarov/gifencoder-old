package com.gifencoder;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class HashMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
  private final Map<E, Count> elementCounts;
  private int size;

  public HashMultiset() {
    this.elementCounts = new HashMap<>();
    this.size = 0;
  }

  public HashMultiset(Collection<E> source) {
    this();
    for (E element : source) {
      add(element);
    }
  }

  @Override public void add(E element, int n) {
    if (elementCounts.containsKey(element)) {
      elementCounts.get(element).value += n;
    } else {
      elementCounts.put(element, new Count(n));
    }
    size += n;
  }

  @Override public boolean add(E element) {
    add(element, 1);
    return true;
  }

  @Override public int remove(Object element, int n) {
    if (elementCounts.containsKey(element)) {
      Count count = elementCounts.get(element);
      int removed;
      if (n >= count.value) {
        elementCounts.remove(element);
        removed = count.value;
      } else {
        count.value -= n;
        removed = n;
      }
      size -= removed;
      return removed;
    } else {
      return 0;
    }
  }

  @Override public boolean remove(Object element) {
    return remove(element, 1) > 0;
  }

  @Override public Iterator<E> iterator() {
    return new HashMultisetIterator();
  }

  @Override public int size() {
    return size;
  }

  @Override public int count(Object element) {
    Count countOrNull = elementCounts.get(element);
    return countOrNull != null ? countOrNull.value : 0;
  }

  @Override public Set<E> getDistinctElements() {
    return elementCounts.keySet();
  }

  private final class HashMultisetIterator implements Iterator<E> {
    final Iterator<E> distinctElementIterator;
    E currentElement;
    int currentCount;
    boolean currentElementRemoved;

    HashMultisetIterator() {
      this.distinctElementIterator = getDistinctElements().iterator();
      this.currentCount = 0;
    }

    @Override public boolean hasNext() {
      return currentCount > 0 || distinctElementIterator.hasNext();
    }

    @Override public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException("iterator has been exhausted");
      }

      if (currentCount == 0) {
        currentElement = distinctElementIterator.next();
        currentCount = count(currentElement);
      }

      --currentCount;
      currentElementRemoved = false;
      return currentElement;
    }

    @Override public void remove() {
      if (currentElement == null) {
        throw new IllegalStateException("next() has not been called");
      }
      if (currentElementRemoved) {
        throw new IllegalStateException("remove() already called for current element");
      }
      HashMultiset.this.remove(currentElement);
    }
  }

  private static final class Count {
    int value;

    Count(int value) {
      this.value = value;
    }
  }
}
