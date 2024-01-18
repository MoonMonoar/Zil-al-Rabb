package com.immo2n.halalife.DataObjects;

public class CommonResponse {
    boolean status;
    String comment;

    public boolean isStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isStatusOk() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
