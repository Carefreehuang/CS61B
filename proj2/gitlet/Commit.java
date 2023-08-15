package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    public String message; // 提交信息
    public Date currentTime; //当前时间
    public String timeStamp; //时间戳
    public List<String> parentsID;//父提交的ID
    //public Hashtable<String,File> blobID; //指向的blob对象
    public Hashtable<String,File> blobID;  //存储 blob对象 ——> 追踪文件名的映射 文件名，文件File（存在BLOB/blobID）
    public String commitID;
    /* TODO: fill in the rest of this class. */
    public Commit(String message, Date date){
        this.message = message;
        this.currentTime = date;
        this.timeStamp = dateToTimeStamp(date);
        this.parentsID = new ArrayList<>();
        this.blobID = new Hashtable<>();
    }

    public String generateID(){
        return Utils.sha1(message,timeStamp,parentsID.toString(),blobID.toString());
    }
    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }
    public static Commit copyParent(String message, Date date){//从parent生成commit
        Tree tree = Utils.readObject(Repository.TREE, Tree.class);
        Commit parentCommit = Repository.headcommit();//获取parentcommit
        Commit commit = new Commit(message, new Date());
        commit.blobID = parentCommit.blobID;
        //System.out.println("add前" + commit.parentsID.size());
        commit.parentsID.add(parentCommit.commitID);
        //System.out.println("add后" + commit.parentsID.size());
        return commit;
    }
    public Commit returnParent(){//返回第一个父commit
        File parentfile = join(Repository.OBJECTS_DIR,this.parentsID.get(0));
        return Utils.readObject(parentfile,Commit.class);
    }

}
