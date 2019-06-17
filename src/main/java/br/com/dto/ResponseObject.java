package br.com.dto;

import java.io.Serializable;

public class ResponseObject implements Serializable {

    private String message;
    private String file;
    private byte[] bytes;

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getFile() { return file; }

    public void setFile(String file) { this.file = file; }

    public byte[] getBytes() { return bytes; }

    public void setBytes(byte[] bytes) { this.bytes = bytes; }
}
