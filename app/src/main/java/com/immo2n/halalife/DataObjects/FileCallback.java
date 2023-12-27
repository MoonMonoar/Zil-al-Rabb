package com.immo2n.halalife.DataObjects;

public class FileCallback {
    public static final String FLAG_FAILED = "fail",
            FLAG_SUCCESS = "success",
            FLAG_PROGRESS = "progress";
    private String status, reason, file;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    private int progress;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
