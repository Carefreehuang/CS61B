package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Utils.join;

public class Tree implements Serializable {
    // Search tree using ID as key and file location as value, used for file lookup.

    public TreeMap<String, File> treeMap;

    public Tree(){
        treeMap = new TreeMap<>();
    }

    public void put(String key, File file){
        treeMap.put(key, file);
    }

    public void saveTree(){
        Utils.writeObject(Repository.TREE, this);
    }

    public File search(String ID){
        return treeMap.get(ID);
    }
}
