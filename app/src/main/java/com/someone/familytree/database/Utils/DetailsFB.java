package com.someone.familytree.database.Utils;

public class DetailsFB{

    String detail;
    String detailName;
    int detailType;
    public String uId;

    public DetailsFB() {
    }


    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public int getDetailType() {
        return detailType;
    }

    public void setDetailType(int detailType) {
        this.detailType = detailType;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}