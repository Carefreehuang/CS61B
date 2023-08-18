package gitlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");//存储commit对象
    public static final File BLOB_DIR = join(GITLET_DIR,"blob"); //存储blob
    public static final File HEADS_DIR = join(GITLET_DIR, "heads");
    public static final File ADDSTAGE = join(GITLET_DIR, "addstage");
    public static final File REMOVESTAGE = join(GITLET_DIR, "removestage");
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File TREE = join(Repository.GITLET_DIR, "tree");
    public static final File MASTER = join(HEADS_DIR,"master");
    /* TODO: fill in the rest of this class. */
    public static void init()  {
        initGitlet("init");
        GITLET_DIR.mkdir();  //创建基本目录
        OBJECTS_DIR.mkdir();
        HEADS_DIR.mkdir();
        BLOB_DIR.mkdir();
        AddStage addstage = new AddStage();
        Utils.writeObject(ADDSTAGE,addstage);
        RemoveStage removestage = new RemoveStage();
        Utils.writeObject(REMOVESTAGE,removestage);
        Tree tree = new Tree();  //生成保存文件的树
        Commit initialCommit = new Commit("initial commit",new Date(0));  //生成initialcommit
        initialCommit.commitID = initialCommit.generateID();//给初始节点添加自身ID
        File initialCommitFile = new File(OBJECTS_DIR,initialCommit.generateID());  //以ID保存commit文件
        writeObject(initialCommitFile,initialCommit);  //生成commit文件
        tree.put(initialCommit.generateID(),initialCommitFile); //将commit map 到 tree
        tree.saveTree();//保存tree
        try {
            Files.writeString(HEAD.toPath(),"master");
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
        }
        try {
            Files.writeString(MASTER.toPath(),initialCommit.generateID());
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
        }

        //writeObject(HEAD,initialCommit.generateID());//创建head，将head指向initcommit
        //writeObject(MASTER,initialCommit.generateID());//创建master，将master指向initcommit
    }
    public static void add(String fileName){
        //如果文件的当前工作版本与当前提交中的版本完全相同，则不要将其添加到暂存区域；
        // 如果文件已经在暂存区域，则应将其从暂存区域移除（当文件被修改、添加，
        // 然后又改回原始版本时可能会发生这种情况）。
        initGitlet("add");//判断是否init
        File addfile = join(CWD, fileName);//获取要添加的文件
        if (!addfile.exists()){    //添加的文件不存在，exit
            System.out.println("File does not exist.");
            System.exit(0);
        } else{
            Blob blob = new Blob(fileName,readContentsAsString(addfile));//生成blob对象
            RemoveStage removeStage = readObject(REMOVESTAGE, RemoveStage.class); //获取removestage
            if (removeStage.hashMap.containsKey(fileName)){
                //如果暂存当前暂存已经有的文件（相同blob）那么不添加并且将removestage中的文件删除
                removeStage.hashMap.remove(fileName);
                removeStage.save();
            }
            if (!blobExist(blob.generateID())){ //如果不存在相同ID的blob，那么将blob写入obj，否则什么也不做
                File blobfile = new File(BLOB_DIR,blob.generateID());
                Tree tree = readObject(TREE,Tree.class);//打开tree
                tree.put(blob.generateID(),blobfile);
                writeObject(blobfile,blob); //保存blob到OBJ
                tree.saveTree();
                AddStage stage = readObject(ADDSTAGE,AddStage.class);//读取stage
                stage.put(fileName,blobfile);//
                //System.out.println( stage.hashMap.size());
                stage.save();
            }
        }

    }
    public static void commit(String message) {
        initGitlet("commit");//判断是否init
        if (message.isBlank()){ //判断message是否为空
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit commit = Commit.copyParent(message,new Date()); //父节点复制一个commit
        AddStage stage = readObject(ADDSTAGE, AddStage.class);
        RemoveStage removestage = readObject(REMOVESTAGE, RemoveStage.class);
        if (stage.isEmpty()){ //暂存区为空
            if (removestage.isEmpty()) {//remove也为空
                System.out.println("No changes added to the commit.");
                System.exit(0);
            }
        }
        Collection<String> filenames = removestage.hashMap.keySet();
        for (String filename : filenames) {
            commit.blobID.remove(filename,removestage.hashMap.get(filename));
        }
        //如何判断commit存储文件
//        Enumeration<File> stagekeys = stage.hashMap.keys();
//        while(stagekeys.hasMoreElements()){
//            File stageblob = stagekeys.nextElement();
//            String stagefilename = stage.hashMap.get(stageblob);
//            commit.blobID.put(stagefilename,stageblob);
//            //在 Java 中，可以使用 put() 方法来修改 Hashtable 中的键值对。
//            // 如果指定的键已经存在于 Hashtable 中，put() 方法将更新对应的值；
//            // 如果键不存在，则会添加新的键值对。
//        }
        //缺少工作目录删除文件的情况
        Collection<String> removefilenames = stage.hashMap.keySet();
        for (String filename : removefilenames) {
            commit.blobID.put(filename,stage.hashMap.get(filename));
        }
        commit.commitID = commit.generateID();
        File commitfile = new File(OBJECTS_DIR, commit.generateID());
        writeObject(commitfile, commit);
        Tree tree = readObject(TREE,Tree.class);//生成tree
        tree.put(commit.generateID(),commitfile);
        tree.saveTree();
        //branch出了问题！！！
//        Files.writeString(HEAD.toPath(),commit.generateID());//更新head
//        Files.writeString(MASTER.toPath(),commit.generateID());//跟新master，应该更新currentbranch而不是一味更新master
        try {
            Files.writeString(join(HEADS_DIR,curretnBranch()).toPath(),commit.generateID());//跟新master
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
        }
        try {
            Files.writeString(HEAD.toPath(),curretnBranch());//更新head
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
        }
        //System.out.println(curretnBranch());
        // Files.writeString(join(HEADS_DIR,curretnBranch()).toPath(),commit.generateID());
        removestage.clear();
        removestage.save();
        stage.clear();//清空stage；
        stage.save();
    }
    public static void rm(String fileName){
        initGitlet("rm");
        AddStage stage = Utils.readObject(ADDSTAGE,AddStage.class);//获取stage对象
        Commit headcommit = headcommit();//获取当前head指向的commit
        if (stage.hashMap.containsKey(fileName)){   //如果在暂存区中，那么取消暂存
            stage.hashMap.remove(fileName,stage.hashMap.get(fileName));
            stage.save();
        } else if (headcommit.blobID.containsKey(fileName)) {//如果被追踪
            //错误提示，contains,containsKey,containsValue不能乱用
            RemoveStage removestage = Utils.readObject(REMOVESTAGE,RemoveStage.class);//获得removestage对象
            removestage.put(fileName,headcommit.blobID.get(fileName));  //添加至removestage
            removestage.save();//保存removstage
            File deletefile = join(CWD,fileName);
            Utils.restrictedDelete(deletefile);//删除文件
        }else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }
    public static void log(){
        initGitlet("log");
        Commit commit = headcommit();
        // System.out.println("commitid" + commit.commitID);
        //  System.out.println("parentid" + commit.parentsID);
        while(commit.parentsID.size()!=0){//为什么一开始的commit.parentID != null不行？永远超过索引
            printLog(commit);
            commit = commit.returnParent();
        }
        System.out.println("===");
        System.out.println("commit " + commit.commitID);
        System.out.println("Date: " + commit.timeStamp);//忘记冒号 搞死我了
        System.out.println(commit.message);
        System.out.println();
    }
    public static void globallog(){//输出全部commit
        initGitlet("global-log");
        List<String> filelist = Utils.plainFilenamesIn(OBJECTS_DIR);
        for (String filename:filelist){   //不知道如何从commit和blob中遍历来查找commit
            File commitfile = join(OBJECTS_DIR,filename);
            Commit commit = readObject(commitfile, Commit.class);
            printLog(commit);
        }
    }
    public static void status(){
        initGitlet("status");
        System.out.println("=== Branches ===");
        printbranches(); //打印branches
        System.out.println("=== Staged Files ===");
        printstage("add");
        System.out.println("=== Removed Files ===");
        printstage("remove");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    public static void find(String message){//根据commit message 输出 commitid
        initGitlet("find");
        boolean idExist = false;//是否存在 匹配的commit
        List<String> filelist = Utils.plainFilenamesIn(OBJECTS_DIR);
        for (String filename:filelist){     //遍历commit
            File commitfile = join(OBJECTS_DIR,filename);
            Commit commit = readObject(commitfile, Commit.class);
            if (commit.message.equals(message)){
                idExist = true;
                System.out.println(commit.commitID);
            }
        }
        if (!idExist){//如果 不存在 匹配commit
            System.out.println("Found no commit with that message.");
        }
    }

    public static void checkout(String[] args){
        initGitlet("checkout");
        if (args.length == 3 && args[1].equals("--")){  //checkout -- [file name]
            String filename = args[2];
            Commit headcommit = headcommit();
            if (!headcommit.blobID.containsKey(filename)){//如果head中没有该文件名
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }else {
                cwdGetFile(headcommit,filename);//将headcommit中的指定file添加到CWD
            }
        } else if (args.length == 4 && args[2].equals("--")) {  //checkout [commit id] -- [file name]
            String commitID = args[1];
            String filename = args [3];
            if (!fileContains(OBJECTS_DIR,commitID)){//如果不存在该commit
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }else {//存在commit
                Commit commit = readObject(join(OBJECTS_DIR,commitID), Commit.class);//获取指定commit
                if (!commit.blobID.containsKey(filename)){//如果commit不包含该文件名
                    System.out.println("File does not exist in that commit.");
                    System.exit(0);
                }else {
                    cwdGetFile(commit,filename);//将commit中的指定file添加到CWD
                }
            }
        } else if (args.length == 2) {  //checkout [branch name]
            String branchname = args[1];
            if (!fileContains(HEADS_DIR, branchname)){//如果branch不存在
                System.out.println("No such branch exists.");
                System.exit(0);
            }else {
                if (curretnBranch().equals(branchname)){//branchname是当前branch
                    System.out.println("No need to checkout the current branch.");
                    System.exit(0);
                }else {//分情况讨论
                    Trackerro(branchname);
                }
            }
        }else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    public static void branch(String branchname)  {//创建分支
        initGitlet("branch");
        if (fileContains(HEADS_DIR,branchname)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File newbranchfile = join(HEADS_DIR,branchname);
        try {
            Files.writeString(newbranchfile.toPath(),headID());//创建分支文件，里面保存当前headID
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
        }

    }
    public static void rmbranch(String branchname) {//删除分支
        initGitlet("rmbranch");
        if (!fileContains(HEADS_DIR,branchname)){  //如果不存在此分支
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }else if (branchname.equals(curretnBranch())){//如果删除当前分支
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }else {
            File file = join(HEADS_DIR,branchname);
            //System.out.println(file);
            //restrictedDelete(file);//删除分支文件,只能删除非隐藏目录的文件
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
            }
        }
    }
    public static void reset(String commitID) {
        if (!fileContains(OBJECTS_DIR,commitID)){//如果不存在该commit
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }else {
            Commit headcommit = headcommit();//获取headcommit
            Commit newcommit = readObject(join(OBJECTS_DIR,commitID), Commit.class);//获取newcommit
            Set<String> headSet= headcommit.blobID.keySet(); //当前headcommit所有的追踪文件的set
            Set<String> newSet = newcommit.blobID.keySet(); //当前newcommit所有的追踪文件的set
            for (String newtrackfile:newSet){//但前commit未追踪，提取的追踪了，并且cwd中有该文件要覆盖
                if (!headSet.contains(newtrackfile) && fileContains(CWD,newtrackfile)){
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
            for (String headtrackfile:headSet){
                if (!newSet.contains(headtrackfile)){//如果提取的追踪不包含当前的追踪
                    restrictedDelete(join(CWD,headtrackfile));//如果当前目录有该文件，则删除head追踪的文件
                }
            }
            for (String newtrackfile:newSet){
                cwdGetFile(newcommit,newtrackfile);//写入文件到CWD
            }
            try {
                Files.writeString((join(HEADS_DIR,curretnBranch()).toPath()), commitID);//修改当前的活动分支
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
            }
            try {
                Files.writeString(HEAD.toPath(),curretnBranch());//修改当前的活动分支
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
            }
            AddStage stage = readObject(ADDSTAGE,AddStage.class);
            stage.clear();
            stage.save();
        }
    }
    public static void Trackerro(String branchname) {//检测是否会有当前未追踪但是提取追踪的情况
        Commit headcommit = headcommit();//返回headcommit
        String newcommitID = readContentsAsString(join(HEADS_DIR,branchname));//newbranch的ID
        Commit newcommit = readObject(join(OBJECTS_DIR,newcommitID), Commit.class);//获取newcommit
        Set<String> headSet= headcommit.blobID.keySet(); //当前headcommit所有的追踪文件的set
        Set<String> newSet = newcommit.blobID.keySet(); //当前newcommit所有的追踪文件的set
        for (String newtrackfile:newSet){//但前commit未追踪，提取的追踪了，并且cwd中有该文件要覆盖
            if (!headSet.contains(newtrackfile) && fileContains(CWD,newtrackfile)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String headtrackfile:headSet){
            if (!newSet.contains(headtrackfile)){//如果提取的追踪不包含当前的追踪
                restrictedDelete(join(CWD,headtrackfile));//如果当前目录有该文件，则删除head追踪的文件
                //System.out.println("删了没");
            }
        }
        for (String newtrackfile:newSet){
            cwdGetFile(newcommit,newtrackfile);//写入文件到CWD
        }
        try {
            Files.writeString(HEAD.toPath(),branchname);//修改当前的活动分支
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
        }
    }

    public static void merge(String branchname){
        initGitlet("merge");
        String splitcommitID = splitpoint(branchname);
        Commit splitcommit = getcommit(splitpoint(branchname));
        //System.out.println(splitcommitID);
        if (splitcommitID.equals(headID())){//如果分割点就是当前分支
            try {
                Files.writeString(HEAD.toPath(),readContentsAsString(join(HEADS_DIR,branchname)));//更新head至该branch
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Current branch fast-forwarded");
            System.exit(0);
        } else if (splitcommitID.equals(readContentsAsString(join(HEADS_DIR,branchname)))) {//如果分割点等于给定分支
            System.out.println("Given branch is an ancestor of the current branch");
            System.exit(0);
        }else {
            String currentbranchname = readContentsAsString(HEAD);
            String message = "Merged " + branchname +" into " + currentbranchname+".";
            Commit mergecommit = new Commit(message,new Date());
            Commit headcommit =headcommit();
            Commit branchcommit = getcommit(readContentsAsString(join(HEADS_DIR,branchname)));  //获取指定commit
            Set<String> hset = headcommit.blobID.keySet();
            Set<String> sset = splitcommit.blobID.keySet();
            Set<String> bset = branchcommit.blobID.keySet();
            TreeMap<String,File> allmap = new TreeMap<>();//保存所有
            Set<String> fileset = fileset(mergecommit, headcommit, branchcommit);//三个commit追踪的所有的文件
            for (String filename : fileset){
                if (!sset.contains(filename)){//sset未追踪
                    if (hset.contains(filename) && bset.contains(filename)){//h和b都包含
                        if (headcommit.blobID.get(filename).equals(branchcommit.blobID.get(filename))){
                            if (!fileContains(CWD,filename)){ //cwd包含则不动，不包含则添加至cwd
                                cwdGetFile(headcommit,filename);
                            }
                            //mergecommit.blobID.put(filename,headcommit.blobID.get(filename));
                        }else {//h！= b   冲突
                            conflict(filename,headcommit,branchcommit);
                        }
                    } else {//都不包含，或者 一个包含一个不包含   s，b无，h有
                        if (hset.contains(filename)){
                            //mergecommit.blobID.put(filename,headcommit.blobID.get(filename));
                            //restrictedDelete(join(CWD,filename));
                        } else if (bset.contains(filename)) { //s，h无，b有
                            mergecommit.blobID.put(filename,branchcommit.blobID.get(filename));
                            cwdGetFile(branchcommit,filename);//添加文件
                            //add(filename);//添加至暂存区
                        }
                    }
                }else {//sset已追踪
                    if (hset.contains(filename)){ //h追踪
                        if (bset.contains(filename)){ //b追踪
                            if (headcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename)) && !branchcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename))){
                                //h = s,b != s
                                mergecommit.blobID.put(filename,branchcommit.blobID.get(filename));
                                cwdGetFile(branchcommit,filename);//添加文件
                                //add(filename);
                            }
                            if (!headcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename)) && branchcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename))) {
                                //h != s,b = s
                                //mergecommit.blobID.put(filename,headcommit.blobID.get(filename));
                            }
                            if (!headcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename)) && !branchcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename))) {
                                //h != s,b != s
                                conflict(filename,headcommit,branchcommit);
                            }
                        }else { //b不追踪
                            restrictedDelete(join(CWD,filename));
                            //rm(filename);//取消追踪
                        }
                    } else { //h不追踪
                        if (bset.contains(filename)){//b追踪    !!!!
                            if (!branchcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename)))
                                //b != s
                                //mergecommit.blobID.put(filename,branchcommit.blobID.get(filename));
                                cwdGetFile(branchcommit,filename);
                        }
                        else {//b不追踪
                            //33 没进来
                            //restrictedDelete(join(CWD,"f.txt"));
                            restrictedDelete(join(CWD,filename));
                        }
                    }
                }
            }
            File branchpointfile = join(HEADS_DIR,branchname);
            mergecommit.parentsID.add(headID());//给merge设定parents
            mergecommit.parentsID.add(readContentsAsString(branchpointfile));
            mergecommit.commitID = mergecommit.generateID();
            writeObject(join(OBJECTS_DIR,mergecommit.commitID), mergecommit);//创建commit文件
            try {
                Files.writeString(HEAD.toPath(),branchname);//更新head
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
            }
            try {
                Files.writeString(branchpointfile.toPath(),mergecommit.commitID);//更新other存贮的ID信息
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
            }

        }
        //checkout(new String[]{branchname});
    }
    public static void conflict(String filename,Commit headcommit,Commit branchcommit){

    }
    public static void addset( Set<String> fileset, Commit commit){//添加追踪文件名
        for (String filename : commit.blobID.keySet()){
            if ( !fileset.contains(filename) ){
                fileset.add(filename);
            }
        }
    }
    public static Set<String> fileset(Commit commit1,Commit commit2,Commit commit3){//生成所有追踪文件的set
        Set<String> fileset = new TreeSet<>();
        addset(fileset, commit1);
        addset(fileset, commit2);
        addset(fileset, commit3);
        return fileset;
    }
    public static String splitpoint(String branchename){//找到splitpoint
        String newcommitID = readContentsAsString(join(HEADS_DIR,branchename));
        TreeMap<String,Integer> oldmap = commitBFS(headcommit());//映射,commitID,距离initial距离  本身的映射
        TreeMap<String,Integer> newmap = commitBFS(getcommit(newcommitID));//映射,commitID,距离initial距离   新指定的映射
        int minidistance = 99999999;//设立大数用做比较
        String minicommitID = newcommitID;
        for (String key:oldmap.keySet()) {//遍历oldmap
            if (newmap.containsKey(key) && oldmap.get(key) < minidistance){
                minidistance = oldmap.get(key);
                minicommitID = key;
            }
        }
        return minicommitID;
    }
    public static Commit getcommit(String commitID){//返回指定commit
        //System.out.println(commitID);
        return readObject(join(OBJECTS_DIR,commitID),Commit.class);
    }
    public static List<Commit> adjcommit(Commit commit){//返回一个commit的相邻commit，即commit的parentcommit
        List<Commit> adjcommit = new ArrayList<>();
        for (String parentID: commit.parentsID) {
            adjcommit.add(getcommit(parentID));
        }
        return adjcommit;
    }
    public static TreeMap<String,Integer> commitBFS(Commit commit){//用bfs遍历，存储所有到该commit的<commitID, distance>
        TreeMap<String,Integer>  map = new TreeMap<>();//存储commitID与该分支的距离
        //int distence = 0;//距离 最初的 距离
        ArrayDeque<Commit> deque = new ArrayDeque<>();  //存放commit
        List<String> visit = new ArrayList<>();  //判断commit是否被访问过
        deque.add(commit);//入队
        visit.add(commit.commitID);//添加至已访问列表
        map.put(commit.commitID, 0);
        while( !deque.isEmpty() ){
            Commit currentcommit = deque.remove();
            for (String adjcommitID : currentcommit.parentsID){
                if (!visit.contains(adjcommitID)){//如果未被访问
                    visit.add(adjcommitID);//标记为访问
                    deque.add(getcommit(adjcommitID));//入队
                    map.put(adjcommitID,map.get(currentcommit.commitID) + 1 );
                    if (getcommit(adjcommitID).message.equals("initial commit")){
                        //判断是否到initialcommit
                        return map;
                    }
                }
            }
        }
        return map;
    }


    public static String curretnBranch(){  //返回当前branch名
//        List<String> filelist = Utils.plainFilenamesIn(HEADS_DIR);
//        for (String filename:filelist) {//遍历heads文件夹
//            File file = join(HEADS_DIR,filename);//当前file
//            if (readContentsAsString(file).equals(headID())){//如果head内容等于此分支内容，即找到主分支
//                return filename;
//            }
//        }
//        return null;
        return readContentsAsString(HEAD);
    }
    public static void cwdGetFile(Commit commit ,String filename)  {//添加commit中的指定file到CWD
        File blobfile = commit.blobID.get(filename);//获取blobfile
        Blob blob = readObject(blobfile, Blob.class);//获取blob
        File newfile = join(CWD,blob.fileName);//创建blob.filename为名字的文件到CWD
        try {
            Files.writeString(newfile.toPath(), blob.fileContent);//将filecontent写入文件，并产生文件
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
        }

    }
    public static boolean fileContains(File FILE,String filename){    //判断该目录下是否存在该文件名
        List<String> filelist = Utils.plainFilenamesIn(FILE);
        for (String file:filelist){
            if (file.equals(filename)){
                return true;
            }
        }
        return false;
    }
    public static void printbranches(){
        List<String> filelist = Utils.plainFilenamesIn(HEADS_DIR);
        for (String filename:filelist) {//遍历heads文件夹
            File file = join(HEADS_DIR,filename);//当前file
            if (readContentsAsString(file).equals(headID())){//如果head内容等于此分支内容，即找到主分支
                System.out.println("*" + filename);
            }else{
                System.out.println(filename);
            }
        }
        System.out.println();
    }
    public static void printstage(String name){//打印stage区域存储的文件名
        if (name.equals("add")){
            AddStage stage = readObject(ADDSTAGE,AddStage.class);//读取已经保存的stage
            Collection<String> stagefiles = stage.hashMap.keySet();
            //System.out.println(stage.hashMap.size());
            for (String stagefile : stagefiles) {
                System.out.println(stagefile);
            }
        }else {
            RemoveStage stage = readObject(REMOVESTAGE,RemoveStage.class);
            Collection<String> stagefiles = stage.hashMap.keySet();
            for (String stagefile : stagefiles) {
                System.out.println(stagefile);
            }
        }
        System.out.println();
    }
    public static void printLog(Commit commit){
        if (commit.parentsID.size() == 1){
            System.out.println("===");
            System.out.println("commit " + commit.commitID);
            System.out.println("Date: " + commit.timeStamp);
            System.out.println(commit.message);
            System.out.println();
        }else if(commit.parentsID.size() == 2) {
            System.out.println("===");
            System.out.println("commit " + commit.commitID);
            //System.out.println("parentsize  " + commit.parentsID.size());
            String parent1 = commit.parentsID.get(0).substring(0,7);
            String parent2 = commit.parentsID.get(1).substring(0,7);
            System.out.println("Merge: " + parent1 + " " + parent2);//parentsize怎么会是0呢
            System.out.println("Date: " + commit.timeStamp);
            System.out.println(commit.message);
            //System.out.println("Merged development into master.");
            System.out.println();
        }else {
            System.out.println("===");
            System.out.println("commit " + commit.commitID);
            System.out.println("Date: " + commit.timeStamp);
            System.out.println(commit.message);
            System.out.println();
        }

    }
    public static String headID(){
        return readContentsAsString(join(HEADS_DIR,readContentsAsString(HEAD)));
    }
    public static Commit headcommit(){ //返回头节点指向的commit
        File headcommitfile = join(OBJECTS_DIR,headID());
        return readObject(headcommitfile, Commit.class);
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
    public static boolean blobExist(String blob){  //判断obje中是否有该blob
        File blobfile = join(BLOB_DIR, blob);
        return blobfile.exists();
    }
}