package com.immo2n.halalife.DataObjects;

import java.util.List;

public class CreatorPayload {
    int mode;
    int privacy;
    List<String> fileList;
    String body;

    public CreatorPayload(int mode, int privacy, List<String> fileList, String body) {
        this.mode = mode;
        this.privacy = privacy;
        this.fileList = fileList;
        this.body = body;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
