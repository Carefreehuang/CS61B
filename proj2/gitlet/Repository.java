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
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");//�洢commit����
    public static final File BLOB_DIR = join(GITLET_DIR,"blob"); //�洢blob
    public static final File HEADS_DIR = join(GITLET_DIR, "heads");
    public static final File ADDSTAGE = join(GITLET_DIR, "addstage");
    public static final File REMOVESTAGE = join(GITLET_DIR, "removestage");
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File TREE = join(Repository.GITLET_DIR, "tree");
    public static final File MASTER = join(HEADS_DIR,"master");
    /* TODO: fill in the rest of this class. */
    public static void init()  {
        initGitlet("init");
        GITLET_DIR.mkdir();  //��������Ŀ¼
        OBJECTS_DIR.mkdir();
        HEADS_DIR.mkdir();
        BLOB_DIR.mkdir();
        AddStage addstage = new AddStage();
        Utils.writeObject(ADDSTAGE,addstage);
        RemoveStage removestage = new RemoveStage();
        Utils.writeObject(REMOVESTAGE,removestage);
        Tree tree = new Tree();  //���ɱ����ļ�����
        Commit initialCommit = new Commit("initial commit",new Date(0));  //����initialcommit
        initialCommit.commitID = initialCommit.generateID();//����ʼ�ڵ��������ID
        File initialCommitFile = new File(OBJECTS_DIR,initialCommit.generateID());  //��ID����commit�ļ�
        writeObject(initialCommitFile,initialCommit);  //����commit�ļ�
        tree.put(initialCommit.generateID(),initialCommitFile); //��commit map �� tree
        tree.saveTree();//����tree
        try {
            Files.writeString(HEAD.toPath(),"master");
        } catch (IOException e) {
            e.printStackTrace(); // ��ӡ�쳣��Ϣ
        }
        try {
            Files.writeString(MASTER.toPath(),initialCommit.generateID());
        } catch (IOException e) {
            e.printStackTrace(); // ��ӡ�쳣��Ϣ
        }

        //writeObject(HEAD,initialCommit.generateID());//����head����headָ��initcommit
        //writeObject(MASTER,initialCommit.generateID());//����master����masterָ��initcommit
    }
    public static void add(String fileName){
        //����ļ��ĵ�ǰ�����汾�뵱ǰ�ύ�еİ汾��ȫ��ͬ����Ҫ������ӵ��ݴ�����
        // ����ļ��Ѿ����ݴ�������Ӧ������ݴ������Ƴ������ļ����޸ġ���ӣ�
        // Ȼ���ָĻ�ԭʼ�汾ʱ���ܻᷢ�������������
        initGitlet("add");//�ж��Ƿ�init
        File addfile = join(CWD, fileName);//��ȡҪ��ӵ��ļ�
        if (!addfile.exists()){    //��ӵ��ļ������ڣ�exit
            System.out.println("File does not exist.");
            System.exit(0);
        } else{
            Blob blob = new Blob(fileName,readContentsAsString(addfile));//����blob����
            RemoveStage removeStage = readObject(REMOVESTAGE, RemoveStage.class); //��ȡremovestage
            if (removeStage.hashMap.containsKey(fileName)){
                //����ݴ浱ǰ�ݴ��Ѿ��е��ļ�����ͬblob����ô����Ӳ��ҽ�removestage�е��ļ�ɾ��
                removeStage.hashMap.remove(fileName);
                removeStage.save();
            }
            if (!blobExist(blob.generateID())){ //�����������ͬID��blob����ô��blobд��obj������ʲôҲ����
                File blobfile = new File(BLOB_DIR,blob.generateID());
                Tree tree = readObject(TREE,Tree.class);//��tree
                tree.put(blob.generateID(),blobfile);
                writeObject(blobfile,blob); //����blob��OBJ
                tree.saveTree();
                AddStage stage = readObject(ADDSTAGE,AddStage.class);//��ȡstage
                stage.put(fileName,blobfile);//
                //System.out.println( stage.hashMap.size());
                stage.save();
            }
        }

    }
    public static void commit(String message) {
        initGitlet("commit");//�ж��Ƿ�init
        if (message.isBlank()){ //�ж�message�Ƿ�Ϊ��
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit commit = Commit.copyParent(message,new Date()); //���ڵ㸴��һ��commit
        AddStage stage = readObject(ADDSTAGE, AddStage.class);
        RemoveStage removestage = readObject(REMOVESTAGE, RemoveStage.class);
        if (stage.isEmpty()){ //�ݴ���Ϊ��
            if (removestage.isEmpty()) {//removeҲΪ��
                System.out.println("No changes added to the commit.");
                System.exit(0);
            }
        }
        Collection<String> filenames = removestage.hashMap.keySet();
        for (String filename : filenames) {
            commit.blobID.remove(filename,removestage.hashMap.get(filename));
        }
        //����ж�commit�洢�ļ�
//        Enumeration<File> stagekeys = stage.hashMap.keys();
//        while(stagekeys.hasMoreElements()){
//            File stageblob = stagekeys.nextElement();
//            String stagefilename = stage.hashMap.get(stageblob);
//            commit.blobID.put(stagefilename,stageblob);
//            //�� Java �У�����ʹ�� put() �������޸� Hashtable �еļ�ֵ�ԡ�
//            // ���ָ���ļ��Ѿ������� Hashtable �У�put() ���������¶�Ӧ��ֵ��
//            // ����������ڣ��������µļ�ֵ�ԡ�
//        }
        //ȱ�ٹ���Ŀ¼ɾ���ļ������
        Collection<String> removefilenames = stage.hashMap.keySet();
        for (String filename : removefilenames) {
            commit.blobID.put(filename,stage.hashMap.get(filename));
        }
        commit.commitID = commit.generateID();
        File commitfile = new File(OBJECTS_DIR, commit.generateID());
        writeObject(commitfile, commit);
        Tree tree = readObject(TREE,Tree.class);//����tree
        tree.put(commit.generateID(),commitfile);
        tree.saveTree();
        //branch�������⣡����
//        Files.writeString(HEAD.toPath(),commit.generateID());//����head
//        Files.writeString(MASTER.toPath(),commit.generateID());//����master��Ӧ�ø���currentbranch������һζ����master
        try {
            Files.writeString(join(HEADS_DIR,curretnBranch()).toPath(),commit.generateID());//����master
        } catch (IOException e) {
            e.printStackTrace(); // ��ӡ�쳣��Ϣ
        }
        try {
            Files.writeString(HEAD.toPath(),curretnBranch());//����head
        } catch (IOException e) {
            e.printStackTrace(); // ��ӡ�쳣��Ϣ
        }
        //System.out.println(curretnBranch());
        // Files.writeString(join(HEADS_DIR,curretnBranch()).toPath(),commit.generateID());
        removestage.clear();
        removestage.save();
        stage.clear();//���stage��
        stage.save();
    }
    public static void rm(String fileName){
        initGitlet("rm");
        AddStage stage = Utils.readObject(ADDSTAGE,AddStage.class);//��ȡstage����
        Commit headcommit = headcommit();//��ȡ��ǰheadָ���commit
        if (stage.hashMap.containsKey(fileName)){   //������ݴ����У���ôȡ���ݴ�
            stage.hashMap.remove(fileName,stage.hashMap.get(fileName));
            stage.save();
        } else if (headcommit.blobID.containsKey(fileName)) {//�����׷��
            //������ʾ��contains,containsKey,containsValue��������
            RemoveStage removestage = Utils.readObject(REMOVESTAGE,RemoveStage.class);//���removestage����
            removestage.put(fileName,headcommit.blobID.get(fileName));  //�����removestage
            removestage.save();//����removstage
            File deletefile = join(CWD,fileName);
            Utils.restrictedDelete(deletefile);//ɾ���ļ�
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
        while(commit.parentsID.size()!=0){//Ϊʲôһ��ʼ��commit.parentID != null���У���Զ��������
            printLog(commit);
            commit = commit.returnParent();
        }
        System.out.println("===");
        System.out.println("commit " + commit.commitID);
        System.out.println("Date: " + commit.timeStamp);//����ð�� ��������
        System.out.println(commit.message);
        System.out.println();
    }
    public static void globallog(){//���ȫ��commit
        initGitlet("global-log");
        List<String> filelist = Utils.plainFilenamesIn(OBJECTS_DIR);
        for (String filename:filelist){   //��֪����δ�commit��blob�б���������commit
            File commitfile = join(OBJECTS_DIR,filename);
            Commit commit = readObject(commitfile, Commit.class);
            printLog(commit);
        }
    }
    public static void status(){
        initGitlet("status");
        System.out.println("=== Branches ===");
        printbranches(); //��ӡbranches
        System.out.println("=== Staged Files ===");
        printstage("add");
        System.out.println("=== Removed Files ===");
        printstage("remove");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    public static void find(String message){//����commit message ��� commitid
        initGitlet("find");
        boolean idExist = false;//�Ƿ���� ƥ���commit
        List<String> filelist = Utils.plainFilenamesIn(OBJECTS_DIR);
        for (String filename:filelist){     //����commit
            File commitfile = join(OBJECTS_DIR,filename);
            Commit commit = readObject(commitfile, Commit.class);
            if (commit.message.equals(message)){
                idExist = true;
                System.out.println(commit.commitID);
            }
        }
        if (!idExist){//��� ������ ƥ��commit
            System.out.println("Found no commit with that message.");
        }
    }

    public static void checkout(String[] args){
        initGitlet("checkout");
        if (args.length == 3 && args[1].equals("--")){  //checkout -- [file name]
            String filename = args[2];
            Commit headcommit = headcommit();
            if (!headcommit.blobID.containsKey(filename)){//���head��û�и��ļ���
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }else {
                cwdGetFile(headcommit,filename);//��headcommit�е�ָ��file��ӵ�CWD
            }
        } else if (args.length == 4 && args[2].equals("--")) {  //checkout [commit id] -- [file name]
            String commitID = args[1];
            String filename = args [3];
            if (!fileContains(OBJECTS_DIR,commitID)){//��������ڸ�commit
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }else {//����commit
                Commit commit = readObject(join(OBJECTS_DIR,commitID), Commit.class);//��ȡָ��commit
                if (!commit.blobID.containsKey(filename)){//���commit���������ļ���
                    System.out.println("File does not exist in that commit.");
                    System.exit(0);
                }else {
                    cwdGetFile(commit,filename);//��commit�е�ָ��file��ӵ�CWD
                }
            }
        } else if (args.length == 2) {  //checkout [branch name]
            String branchname = args[1];
            if (!fileContains(HEADS_DIR, branchname)){//���branch������
                System.out.println("No such branch exists.");
                System.exit(0);
            }else {
                if (curretnBranch().equals(branchname)){//branchname�ǵ�ǰbranch
                    System.out.println("No need to checkout the current branch.");
                    System.exit(0);
                }else {//���������
                    Trackerro(branchname);
                }
            }
        }else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    public static void branch(String branchname)  {//������֧
        initGitlet("branch");
        if (fileContains(HEADS_DIR,branchname)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File newbranchfile = join(HEADS_DIR,branchname);
        try {
            Files.writeString(newbranchfile.toPath(),headID());//������֧�ļ������汣�浱ǰheadID
        } catch (IOException e) {
            e.printStackTrace(); // ��ӡ�쳣��Ϣ
        }

    }
    public static void rmbranch(String branchname) {//ɾ����֧
        initGitlet("rmbranch");
        if (!fileContains(HEADS_DIR,branchname)){  //��������ڴ˷�֧
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }else if (branchname.equals(curretnBranch())){//���ɾ����ǰ��֧
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }else {
            File file = join(HEADS_DIR,branchname);
            //System.out.println(file);
            //restrictedDelete(file);//ɾ����֧�ļ�,ֻ��ɾ��������Ŀ¼���ļ�
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                e.printStackTrace(); // ��ӡ�쳣��Ϣ
            }
        }
    }
    public static void reset(String commitID) {
        if (!fileContains(OBJECTS_DIR,commitID)){//��������ڸ�commit
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }else {
            Commit headcommit = headcommit();//��ȡheadcommit
            Commit newcommit = readObject(join(OBJECTS_DIR,commitID), Commit.class);//��ȡnewcommit
            Set<String> headSet= headcommit.blobID.keySet(); //��ǰheadcommit���е�׷���ļ���set
            Set<String> newSet = newcommit.blobID.keySet(); //��ǰnewcommit���е�׷���ļ���set
            for (String newtrackfile:newSet){//��ǰcommitδ׷�٣���ȡ��׷���ˣ�����cwd���и��ļ�Ҫ����
                if (!headSet.contains(newtrackfile) && fileContains(CWD,newtrackfile)){
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
            for (String headtrackfile:headSet){
                if (!newSet.contains(headtrackfile)){//�����ȡ��׷�ٲ�������ǰ��׷��
                    restrictedDelete(join(CWD,headtrackfile));//�����ǰĿ¼�и��ļ�����ɾ��head׷�ٵ��ļ�
                }
            }
            for (String newtrackfile:newSet){
                cwdGetFile(newcommit,newtrackfile);//д���ļ���CWD
            }
            try {
                Files.writeString((join(HEADS_DIR,curretnBranch()).toPath()), commitID);//�޸ĵ�ǰ�Ļ��֧
            } catch (IOException e) {
                e.printStackTrace(); // ��ӡ�쳣��Ϣ
            }
            try {
                Files.writeString(HEAD.toPath(),curretnBranch());//�޸ĵ�ǰ�Ļ��֧
            } catch (IOException e) {
                e.printStackTrace(); // ��ӡ�쳣��Ϣ
            }
            AddStage stage = readObject(ADDSTAGE,AddStage.class);
            stage.clear();
            stage.save();
        }
    }
    public static void Trackerro(String branchname) {//����Ƿ���е�ǰδ׷�ٵ�����ȡ׷�ٵ����
        Commit headcommit = headcommit();//����headcommit
        String newcommitID = readContentsAsString(join(HEADS_DIR,branchname));//newbranch��ID
        Commit newcommit = readObject(join(OBJECTS_DIR,newcommitID), Commit.class);//��ȡnewcommit
        Set<String> headSet= headcommit.blobID.keySet(); //��ǰheadcommit���е�׷���ļ���set
        Set<String> newSet = newcommit.blobID.keySet(); //��ǰnewcommit���е�׷���ļ���set
        for (String newtrackfile:newSet){//��ǰcommitδ׷�٣���ȡ��׷���ˣ�����cwd���и��ļ�Ҫ����
            if (!headSet.contains(newtrackfile) && fileContains(CWD,newtrackfile)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String headtrackfile:headSet){
            if (!newSet.contains(headtrackfile)){//�����ȡ��׷�ٲ�������ǰ��׷��
                restrictedDelete(join(CWD,headtrackfile));//�����ǰĿ¼�и��ļ�����ɾ��head׷�ٵ��ļ�
                //System.out.println("ɾ��û");
            }
        }
        for (String newtrackfile:newSet){
            cwdGetFile(newcommit,newtrackfile);//д���ļ���CWD
        }
        try {
            Files.writeString(HEAD.toPath(),branchname);//�޸ĵ�ǰ�Ļ��֧
        } catch (IOException e) {
            e.printStackTrace(); // ��ӡ�쳣��Ϣ
        }
    }

    public static void merge(String branchname){
        initGitlet("merge");
        String splitcommitID = splitpoint(branchname);
        Commit splitcommit = getcommit(splitpoint(branchname));
        //System.out.println(splitcommitID);
        if (splitcommitID.equals(headID())){//����ָ����ǵ�ǰ��֧
            try {
                Files.writeString(HEAD.toPath(),readContentsAsString(join(HEADS_DIR,branchname)));//����head����branch
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Current branch fast-forwarded");
            System.exit(0);
        } else if (splitcommitID.equals(readContentsAsString(join(HEADS_DIR,branchname)))) {//����ָ����ڸ�����֧
            System.out.println("Given branch is an ancestor of the current branch");
            System.exit(0);
        }else {
            String currentbranchname = readContentsAsString(HEAD);
            String message = "Merged " + branchname +" into " + currentbranchname+".";
            Commit mergecommit = new Commit(message,new Date());
            Commit headcommit =headcommit();
            Commit branchcommit = getcommit(readContentsAsString(join(HEADS_DIR,branchname)));  //��ȡָ��commit
            Set<String> hset = headcommit.blobID.keySet();
            Set<String> sset = splitcommit.blobID.keySet();
            Set<String> bset = branchcommit.blobID.keySet();
            TreeMap<String,File> allmap = new TreeMap<>();//��������
            Set<String> fileset = fileset(mergecommit, headcommit, branchcommit);//����commit׷�ٵ����е��ļ�
            for (String filename : fileset){
                if (!sset.contains(filename)){//ssetδ׷��
                    if (hset.contains(filename) && bset.contains(filename)){//h��b������
                        if (headcommit.blobID.get(filename).equals(branchcommit.blobID.get(filename))){
                            if (!fileContains(CWD,filename)){ //cwd�����򲻶����������������cwd
                                cwdGetFile(headcommit,filename);
                            }
                            //mergecommit.blobID.put(filename,headcommit.blobID.get(filename));
                        }else {//h��= b   ��ͻ
                            conflict(filename,headcommit,branchcommit);
                        }
                    } else {//�������������� һ������һ��������   s��b�ޣ�h��
                        if (hset.contains(filename)){
                            //mergecommit.blobID.put(filename,headcommit.blobID.get(filename));
                            //restrictedDelete(join(CWD,filename));
                        } else if (bset.contains(filename)) { //s��h�ޣ�b��
                            mergecommit.blobID.put(filename,branchcommit.blobID.get(filename));
                            cwdGetFile(branchcommit,filename);//����ļ�
                            //add(filename);//������ݴ���
                        }
                    }
                }else {//sset��׷��
                    if (hset.contains(filename)){ //h׷��
                        if (bset.contains(filename)){ //b׷��
                            if (headcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename)) && !branchcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename))){
                                //h = s,b != s
                                mergecommit.blobID.put(filename,branchcommit.blobID.get(filename));
                                cwdGetFile(branchcommit,filename);//����ļ�
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
                        }else { //b��׷��
                            restrictedDelete(join(CWD,filename));
                            //rm(filename);//ȡ��׷��
                        }
                    } else { //h��׷��
                        if (bset.contains(filename)){//b׷��    !!!!
                            if (!branchcommit.blobID.get(filename).equals(splitcommit.blobID.get(filename)))
                                //b != s
                                //mergecommit.blobID.put(filename,branchcommit.blobID.get(filename));
                                cwdGetFile(branchcommit,filename);
                        }
                        else {//b��׷��
                            //33 û����
                            //restrictedDelete(join(CWD,"f.txt"));
                            restrictedDelete(join(CWD,filename));
                        }
                    }
                }
            }
            File branchpointfile = join(HEADS_DIR,branchname);
            mergecommit.parentsID.add(headID());//��merge�趨parents
            mergecommit.parentsID.add(readContentsAsString(branchpointfile));
            mergecommit.commitID = mergecommit.generateID();
            writeObject(join(OBJECTS_DIR,mergecommit.commitID), mergecommit);//����commit�ļ�
            try {
                Files.writeString(HEAD.toPath(),branchname);//����head
            } catch (IOException e) {
                e.printStackTrace(); // ��ӡ�쳣��Ϣ
            }
            try {
                Files.writeString(branchpointfile.toPath(),mergecommit.commitID);//����other������ID��Ϣ
            } catch (IOException e) {
                e.printStackTrace(); // ��ӡ�쳣��Ϣ
            }

        }
        //checkout(new String[]{branchname});
    }
    public static void conflict(String filename,Commit headcommit,Commit branchcommit){

    }
    public static void addset( Set<String> fileset, Commit commit){//���׷���ļ���
        for (String filename : commit.blobID.keySet()){
            if ( !fileset.contains(filename) ){
                fileset.add(filename);
            }
        }
    }
    public static Set<String> fileset(Commit commit1,Commit commit2,Commit commit3){//��������׷���ļ���set
        Set<String> fileset = new TreeSet<>();
        addset(fileset, commit1);
        addset(fileset, commit2);
        addset(fileset, commit3);
        return fileset;
    }
    public static String splitpoint(String branchename){//�ҵ�splitpoint
        String newcommitID = readContentsAsString(join(HEADS_DIR,branchename));
        TreeMap<String,Integer> oldmap = commitBFS(headcommit());//ӳ��,commitID,����initial����  �����ӳ��
        TreeMap<String,Integer> newmap = commitBFS(getcommit(newcommitID));//ӳ��,commitID,����initial����   ��ָ����ӳ��
        int minidistance = 99999999;//�������������Ƚ�
        String minicommitID = newcommitID;
        for (String key:oldmap.keySet()) {//����oldmap
            if (newmap.containsKey(key) && oldmap.get(key) < minidistance){
                minidistance = oldmap.get(key);
                minicommitID = key;
            }
        }
        return minicommitID;
    }
    public static Commit getcommit(String commitID){//����ָ��commit
        //System.out.println(commitID);
        return readObject(join(OBJECTS_DIR,commitID),Commit.class);
    }
    public static List<Commit> adjcommit(Commit commit){//����һ��commit������commit����commit��parentcommit
        List<Commit> adjcommit = new ArrayList<>();
        for (String parentID: commit.parentsID) {
            adjcommit.add(getcommit(parentID));
        }
        return adjcommit;
    }
    public static TreeMap<String,Integer> commitBFS(Commit commit){//��bfs�������洢���е���commit��<commitID, distance>
        TreeMap<String,Integer>  map = new TreeMap<>();//�洢commitID��÷�֧�ľ���
        //int distence = 0;//���� ����� ����
        ArrayDeque<Commit> deque = new ArrayDeque<>();  //���commit
        List<String> visit = new ArrayList<>();  //�ж�commit�Ƿ񱻷��ʹ�
        deque.add(commit);//���
        visit.add(commit.commitID);//������ѷ����б�
        map.put(commit.commitID, 0);
        while( !deque.isEmpty() ){
            Commit currentcommit = deque.remove();
            for (String adjcommitID : currentcommit.parentsID){
                if (!visit.contains(adjcommitID)){//���δ������
                    visit.add(adjcommitID);//���Ϊ����
                    deque.add(getcommit(adjcommitID));//���
                    map.put(adjcommitID,map.get(currentcommit.commitID) + 1 );
                    if (getcommit(adjcommitID).message.equals("initial commit")){
                        //�ж��Ƿ�initialcommit
                        return map;
                    }
                }
            }
        }
        return map;
    }


    public static String curretnBranch(){  //���ص�ǰbranch��
//        List<String> filelist = Utils.plainFilenamesIn(HEADS_DIR);
//        for (String filename:filelist) {//����heads�ļ���
//            File file = join(HEADS_DIR,filename);//��ǰfile
//            if (readContentsAsString(file).equals(headID())){//���head���ݵ��ڴ˷�֧���ݣ����ҵ�����֧
//                return filename;
//            }
//        }
//        return null;
        return readContentsAsString(HEAD);
    }
    public static void cwdGetFile(Commit commit ,String filename)  {//���commit�е�ָ��file��CWD
        File blobfile = commit.blobID.get(filename);//��ȡblobfile
        Blob blob = readObject(blobfile, Blob.class);//��ȡblob
        File newfile = join(CWD,blob.fileName);//����blob.filenameΪ���ֵ��ļ���CWD
        try {
            Files.writeString(newfile.toPath(), blob.fileContent);//��filecontentд���ļ����������ļ�
        } catch (IOException e) {
            e.printStackTrace(); // ��ӡ�쳣��Ϣ
        }

    }
    public static boolean fileContains(File FILE,String filename){    //�жϸ�Ŀ¼���Ƿ���ڸ��ļ���
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
        for (String filename:filelist) {//����heads�ļ���
            File file = join(HEADS_DIR,filename);//��ǰfile
            if (readContentsAsString(file).equals(headID())){//���head���ݵ��ڴ˷�֧���ݣ����ҵ�����֧
                System.out.println("*" + filename);
            }else{
                System.out.println(filename);
            }
        }
        System.out.println();
    }
    public static void printstage(String name){//��ӡstage����洢���ļ���
        if (name.equals("add")){
            AddStage stage = readObject(ADDSTAGE,AddStage.class);//��ȡ�Ѿ������stage
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
            System.out.println("Merge: " + parent1 + " " + parent2);//parentsize��ô����0��
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
    public static Commit headcommit(){ //����ͷ�ڵ�ָ���commit
        File headcommitfile = join(OBJECTS_DIR,headID());
        return readObject(headcommitfile, Commit.class);
    }
    public static void initGitlet(String cmd){//�ж��Ƿ���ڳ�ʼ��Ŀ¼.gitlet
        boolean gitletExist = (GITLET_DIR.exists() && GITLET_DIR.isDirectory()); //.gitlet���ڲ����Ǹ�Ŀ¼
        if (cmd.equals("init")){   //   ��Java�У�''�������ţ����ڱ�ʾ�ַ�����""��˫���ţ����ڱ�ʾ�ַ�����
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
    public static boolean blobExist(String blob){  //�ж�obje���Ƿ��и�blob
        File blobfile = join(BLOB_DIR, blob);
        return blobfile.exists();
    }
}