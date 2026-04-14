package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {

    public static final int RESIZE_MULTIPLIER = 2;
    private final int initialCapacity = 16;
    private final int initialThreshold = 12;
    private int currentCapacity = initialCapacity;
    private Node<K, V>[] table = new Node[currentCapacity];
    private int currentThreshold = initialThreshold;
    private final double defaultLoadFactor = 0.75;
    private int size = 0;

    @Override
    public void put(K key, V value) {
        if (table == null || table.length == 0) {
            currentCapacity = initialCapacity;
            currentThreshold = calculateThreshold(currentCapacity, defaultLoadFactor);
            table = (Node<K, V>[]) new Node[currentCapacity];
        }
        int hash = (key == null) ? 0 : key.hashCode();
        int idx = index(hash, currentCapacity);
        for (Node<K, V> node = table[idx]; node != null; node = node.next) {
            if (node.hash == hash && (node.key == key
                    || (node.key != null && node.key.equals(key)))) {
                node.value = value;
                return;
            }
        }
        Node<K, V> newNode = new Node<>(hash, key, value);
        newNode.next = table[idx];
        table[idx] = newNode;
        size++;
        if (size > currentThreshold) {
            resize();
            currentThreshold = calculateThreshold(currentCapacity, defaultLoadFactor);
        }
    }

    @Override
    public V getValue(K key) {
        if (table == null || table.length == 0) {
            return null;
        } else {
            int hash = (key == null) ? 0 : key.hashCode();
            int idx = index(hash, currentCapacity);
            if (table[idx] == null) {
                return null;
            }
            if (Objects.equals(table[idx].key, key)) {
                return table[idx].value;
            } else {
                Node<K, V> node = table[idx];
                while (node != null) {
                    if (node.hash == hash && (Objects.equals(node.key, key))) {
                        return node.value;
                    }
                    node = node.next;
                }
            }
        }
        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    private int calculateThreshold(int currentCapacity, double defaultLoadFactor) {
        return (int) (defaultLoadFactor * currentCapacity);
    }

    private void resize() {
        int newCapacity = currentCapacity * RESIZE_MULTIPLIER;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];
        for (int i = 0; i < table.length; i++) {
            Node<K, V> node = table[i];
            while (node != null) {
                Node<K, V> next = node.next;
                int newIdx = index(node.hash, newCapacity);
                node.next = newTable[newIdx];
                newTable[newIdx] = node;
                node = next;
            }
        }
        table = newTable;
        currentCapacity = newCapacity;
        currentThreshold = calculateThreshold(currentCapacity, defaultLoadFactor);
    }

    private int index(int hashCode, int currentCapacity) {
        int index = hashCode % currentCapacity;
        if (index < 0) {
            index += currentCapacity;
        }
        return index;
    }

    private static class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }
    }
}
