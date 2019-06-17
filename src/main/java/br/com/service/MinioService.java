package br.com.service;

import br.com.dto.ResponseObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {

    public ResponseObject uploadFileAmazon(String fileName, MultipartFile file);
    public InputStream downloadFileAmazon(String fileName);
    public ResponseObject deleteFileAmazon(String fileName);

}
