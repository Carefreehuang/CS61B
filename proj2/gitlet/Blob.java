package gitlet;

import java.io.Serializable;


public class Blob implements Serializable {
    private String fileName;
    private String fileContent;
    private String blobID;
    public String generateID(){  //生成ID
        return Utils.sha1(this.fileName, this.fileContent);
    }
    public Blob(String fileName, String fileContent){
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.blobID = generateID();
    }
}
