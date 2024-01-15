package com.immo2n.halalife.DataObjects;

import java.io.File;
import java.util.List;

public class MediaSelectionList {
    List<File> fileList;

    public MediaSelectionList(List<File> fileList) {
        this.fileList = fileList;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }
}
