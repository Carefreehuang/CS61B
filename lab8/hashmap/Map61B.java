package hashmap;

import java.util.Set;
/**
 * 你的 hashmap.MyHashMap 实现应该实现这个接口。为了做到这一点，
 * 在你的 "public class..." 声明的结尾添加 "implements hashmap.Map61B<K, V>"，
 * 虽然你也可以使用其他形式的类型参数。
 */
public interface Map61B<K, V> extends Iterable<K> {
    /** 从这个映射中删除所有映射。 */
    void clear();

    /** 如果此映射包含指定键的映射，则返回 true。 */
    boolean containsKey(K key);

    /**
     * 返回指定键映射到的值，如果此映射不包含该键的映射，则返回 null。
     */
    V get(K key);

    /** 返回此映射中的键值对数量。 */
    int size();

    /**
     * 将指定值与指定键关联在此映射中。
     * 如果映射先前包含键的映射，则旧值将被替换。
     */
    void put(K key, V value);

    /** 返回此映射中包含的键的 Set 视图。 */
    Set<K> keySet();

    /**
     * 如果存在，则从该映射中移除指定键的映射。
     * 对于实验 8 不是必需的。如果你不实现这个方法，请抛出 UnsupportedOperationException。
     */
    V remove(K key);

    /**
     * 仅在指定键当前映射到指定值时才删除该键的条目。
     * 对于实验 8 不是必需的。如果你不实现这个方法，请抛出 UnsupportedOperationException。
     */
    V remove(K key, V value);
}
