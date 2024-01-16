package com.immo2n.halalife.DataObjects;

import java.io.File;
import java.util.List;

public class MediaSelectionList {
    List<String> fileList;

    public MediaSelectionList(List<String> fileList) {
        this.fileList = fileList;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }
}
