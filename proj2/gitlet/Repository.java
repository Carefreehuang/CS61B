package gitlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Enumeration;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");//存储blob对象，commit对象
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File ADDSTAGE = join(GITLET_DIR, "addstage");
    public static final File REMOVESTAGE = join(GITLET_DIR, "removestage");
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File TREE = join(Repository.GITLET_DIR, "tree");
    public static final File MASTER = join(HEADS_DIR,"master");
    /* TODO: fill in the rest of this class. */
    public static void init() throws IOException {
        initGitlet("init");
        GITLET_DIR.mkdir();  //创建基本目录
        OBJECTS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        Tree tree = new Tree();  //生成保存文件的树
        Commit initialCommit = new Commit("initial commit",new Date(0));  //生成initialcommit
        File initialCommitFile = new File(OBJECTS_DIR,initialCommit.generateID());  //以ID保存commit文件
        writeObject(initialCommitFile,initialCommit);  //生成commit文件
        tree.put(initialCommit.generateID(),initialCommitFile); //将commit map 到 tree
        tree.saveTree();//保存tree
        Files.writeString(HEAD.toPath(),initialCommit.generateID());
        Files.writeString(MASTER.toPath(),initialCommit.generateID());
        //writeObject(HEAD,initialCommit.generateID());//创建head，将head指向initcommit
        //writeObject(MASTER,initialCommit.generateID());//创建master，将master指向initcommit
    }
    public static void add(String fileName){
        initGitlet("add");//判断是否init
        File addfile = join(CWD, fileName);//获取要添加的文件
        if (!addfile.exists()){    //添加的文件不存在，exit
            System.out.println("File does not exist.");
            System.exit(0);
        } else{
            Blob blob = new Blob(fileName,readContentsAsString(addfile));//生成blob对象
            if (!blobExist(blob.generateID())){ //如果不存在相同ID的blob，那么将blob写入obj，否则什么也不做
                File blobfile = new File(OBJECTS_DIR,blob.generateID());
                Tree tree = readObject(TREE,Tree.class);//打开tree
                tree.put(blob.generateID(),blobfile);
                writeObject(blobfile,blob); //保存blob到OBJ
                tree.saveTree();
                AddStage stage = new AddStage();
                stage.put(blobfile,fileName);//有问题
                stage.save();
            }
        }

    }
    public static void commit(String message) throws IOException {
        initGitlet("commit");//判断是否init
        if (message.isBlank()){ //判断message是否为空
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        AddStage stage = readObject(ADDSTAGE, AddStage.class);
        if (stage.isEmpty()){ //暂存区为空
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit commit = Commit.copyParent(message,new Date()); //父节点复制一个commit
        //如何判断commit存储文件
        Enumeration<File> stagekeys = stage.hashMap.keys();
        while(stage.hashMap.keys().hasMoreElements()){
            File stageblob = stagekeys.nextElement();
            String stagefilename = stage.hashMap.get(stageblob);
            commit.blobID.put(stageblob,stagefilename);
            //在 Java 中，可以使用 put() 方法来修改 Hashtable 中的键值对。
            // 如果指定的键已经存在于 Hashtable 中，put() 方法将更新对应的值；
            // 如果键不存在，则会添加新的键值对。
        }
        File commitfile = new File(OBJECTS_DIR, commit.generateID());
        writeObject(commitfile, commit);
        Tree tree = readObject(TREE,Tree.class);//生成tree
        tree.put(commit.generateID(),commitfile);
        tree.saveTree();
        Files.writeString(HEAD.toPath(),commit.generateID());
        Files.writeString(MASTER.toPath(),commit.generateID());
        stage.clear();//清空stage；
        stage.save();
    }
    public static String headID(){
        return readContentsAsString(HEAD);
    }
    public static void initGitlet(String cmd){//判断是否存在初始化目录.gitlet
        boolean gitletExist = (GITLET_DIR.exists() && GITLET_DIR.isDirectory()); //.gitlet存在并且是个目录
        if (cmd.equals("init")){   //   在Java中，''（单引号）用于表示字符，而""（双引号）用于表示字符串。
            if (gitletExist){
                System.out.println("A Gitlet version-control system already exists in the current directory.");
                System.exit(0);
            }
        }
        else{
            if (!gitletExist){
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
        }
    }
    public static boolean blobExist(String blob){
        File blobfile = join(OBJECTS_DIR, blob);
        return blobfile.exists();
    }
}
