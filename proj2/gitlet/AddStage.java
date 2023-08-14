package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Hashtable;

public class AddStage implements Serializable {
    public Hashtable<String, File> hashMap;
    public AddStage(){
        hashMap = new Hashtable<>();
    }
    public void put(String filename, File blob ){
        hashMap.put(filename,blob);
    }
    public void save(){
        Utils.writeObject(Repository.ADDSTAGE,this);
    }
    public boolean isEmpty(){
        return hashMap.isEmpty();
    }
    public void clear(){
        this.hashMap.clear();
    }
}
