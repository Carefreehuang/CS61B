package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Hashtable;

public class AddStage implements Serializable {
    public Hashtable<File, String> hashMap;
    public AddStage(){
        hashMap = new Hashtable<>();
    }
    public void put(File blob, String filename){
        hashMap.put(blob,filename);
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
