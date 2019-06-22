package dto;

import java.io.Serializable;

/**
 * @author wxy
 * @create 2017-11-29 16:29
 **/
public class UserPhotoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String uid;
    private String fileName;
    private String filePath;
    private String photoPath; // 图片名为img0
    private String frontPath; // 图片名为img1
    private String backPath; // 图片名为bankCardPosiUrl;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getFrontPath() {
        return frontPath;
    }

    public void setFrontPath(String frontPath) {
        this.frontPath = frontPath;
    }

    public String getBackPath() {
        return backPath;
    }

    public void setBackPath(String backPath) {
        this.backPath = backPath;
    }
}
