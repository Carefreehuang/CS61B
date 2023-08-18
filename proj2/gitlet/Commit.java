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

    public String message; // Commit message
    public Date currentTime; // Current time
    public String timeStamp; // Time stamp
    public List<String> parentsID; // IDs of parent commits
    public Hashtable<String, File> blobID;
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
        return Utils.sha1(message, timeStamp, parentsID.toString(), blobID.toString());
    }

    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    public static Commit copyParent(String message, Date date){
        Tree tree = Utils.readObject(Repository.TREE, Tree.class);
        Commit parentCommit = Repository.headcommit();
        Commit commit = new Commit(message, new Date());
        commit.blobID = parentCommit.blobID;
        commit.parentsID.add(parentCommit.commitID);
        return commit;
    }

    public Commit returnParent(){ // Return the first parent commit
        File parentfile = join(Repository.OBJECTS_DIR, this.parentsID.get(0));
        return Utils.readObject(parentfile, Commit.class);
    }
}
