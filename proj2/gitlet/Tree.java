package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Utils.join;

public class Tree implements Serializable {//搜索树，以 ID 为key, 文件位置为 value， 用于查找文件。

    public TreeMap<String, File> treeMap;
    public Tree(){
        treeMap = new TreeMap<>();
    }
    public void put(String key, File file){
        treeMap.put(key, file);
    }
    public void saveTree(){ //保存至tree
        Utils.writeObject(Repository.TREE,this);
    }
    public File search(String ID){  //根据ID返回文件
        return treeMap.get(ID);
    }
}
