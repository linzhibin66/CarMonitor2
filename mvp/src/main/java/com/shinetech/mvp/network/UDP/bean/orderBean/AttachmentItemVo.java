package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by Lenovo on 2018-01-16.
 */

public class AttachmentItemVo extends BaseVo {

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 上传人
     */
    private String uploadPerson;

    /**
     * 上传时间
     */
    private String uploadTime;

    public AttachmentItemVo(String fileType, String fileName, String uploadPerson, String uploadTime) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.uploadPerson = uploadPerson;
        this.uploadTime = uploadTime;
    }

    public AttachmentItemVo() {
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{fileType, fileName, uploadPerson, uploadTime};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.fileType = parseString((byte[]) properties[0]);
        this.fileName = parseString((byte[]) properties[1]);
        this.uploadPerson = parseString((byte[]) properties[2]);
        this.uploadTime = parseString((byte[]) properties[3]);
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, STRING, STRING, STRING};
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadPerson() {
        return uploadPerson;
    }

    public void setUploadPerson(String uploadPerson) {
        this.uploadPerson = uploadPerson;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "AttachmentItemVo{" +
                "fileType='" + fileType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", uploadPerson='" + uploadPerson + '\'' +
                ", uploadTime='" + uploadTime + '\'' +
                '}';
    }
}
