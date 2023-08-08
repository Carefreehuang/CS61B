package hashmap;

import java.util.*;

/**
 * 基于哈希表的映射实现。在最好的情况下，通过 get()、remove() 和 put() 方法提供了平摊常数时间的元素访问。
 *
 * 假设不会插入空键，并且在删除时不会缩小。author HUANGZIHAO
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * 保护的辅助类，用于存储键/值对
     * protected 修饰符允许子类访问
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* 实例变量 */
    private static final int  DEFAULT_INITIALSIZE = 16;
    private static final double DEFAULT_LOADFACTOR = 0.75;
    private Collection<Node>[] buckets;
    private int initialSize;
    private double loadFactor;
    private int bucketSize;
    private int nodeSize;
    private Set<K> keys;  // key的集合
    // 你可能还需要定义一些变量！

    /** 构造函数 */
    public MyHashMap() {
        this(DEFAULT_INITIALSIZE,DEFAULT_LOADFACTOR);   //调用 MyHashMap(int initialSize, double maxLoad)
    }

    public MyHashMap(int initialSize) {
        this(initialSize,DEFAULT_LOADFACTOR);
    }

    /**
     * MyHashMap 构造函数，创建初始大小的支持数组。
     * 负载因子（项数 / 桶数）应始终 <= loadFactor。
     *
     * @param initialSize 支持数组的初始大小
     * @param maxLoad 最大负载因子
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        this.buckets = createTable(this.initialSize);
        this.nodeSize = 0;
        this.bucketSize = initialSize;
        keys = new HashSet<>();
    }

    /**
     * 返回一个新的节点，将放置在哈希表桶中
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * 返回一个数据结构作为哈希表桶
     *
     * 哈希表桶的唯一要求是我们可以：
     *  1. 插入项目（`add` 方法）
     *  2. 移除项目（`remove` 方法）
     *  3. 遍历项目（`iterator` 方法）
     *
     * 每个这些方法都由 java.util.Collection 支持，
     * Java 中的大多数数据结构都继承自 Collection，因此我们可以
     * 使用几乎任何数据结构作为我们的桶。
     *
     * 重写此方法以使用不同的数据结构作为底层桶类型
     *
     * 请确保调用此工厂方法，而不是使用 new 运算符创建自己的桶数据结构！
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * 返回用于支撑哈希表的表格。根据上面的注释，
     * 这个表格可以是 Collection 对象的数组
     *
     * 请务必在创建表格时调用此工厂方法，
     * 以便所有桶类型都是 JAVA.UTIL.COLLECTION
     *
     * @param tableSize 要创建的表格大小
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection[] buckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            buckets[i] = createBucket();
        }
        return buckets;
    }
    private int getIndex(K key){
        return Math.abs(key.hashCode()) % this.bucketSize;
    }

    // TODO：在下面实现 Map61B 接口的方法
    // 你的代码在实现这些方法之前将无法编译！
    @Override
    public int size() {
        return this.nodeSize;
    }

    @Override
    public boolean containsKey(K key) {
        if (keys == null){
            return false;
        }
        return keys.contains(key);
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)){
            return null;
        }
            int index = getIndex(key);
            for (Node node:buckets[index]) {
                if (node.key.equals(key)){
                    return node.value;
                }
            }
            return null;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();

    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void clear() {
        for (int i = 0; i < this.bucketSize; i++) {
            buckets[i] = null;
        }
        keys =null;
        nodeSize = 0;
    }

    @Override
    public void put(K key, V value) {
        int index = getIndex(key);  // 要让映射均匀分布。
        if (this.buckets[index] != null) {//判断bucket是否为空
            if (!containsKey(key)) {
                buckets[index].add(createNode(key, value));
                this.nodeSize += 1;
                keys.add(key);
            } else {
                for (Node node : buckets[index]) {
                    if (node.key.equals(key)) {
                        node.value = value;
                    }
                }
            }
        }
        if (!containsKey(key)) {
            buckets[index].add(createNode(key, value));
            this.nodeSize += 1;
            keys.add(key);
        }
    }



    @Override
    public Set<K> keySet() {
        return keys;
    }
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
