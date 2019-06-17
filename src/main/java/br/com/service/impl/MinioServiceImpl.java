package br.com.service.impl;

import br.com.dto.ResponseObject;
import br.com.service.MinioService;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioServiceImpl implements MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);

    @Autowired
    MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public ResponseObject uploadFileAmazon(String fileName, MultipartFile file) {
        ResponseObject responseObject = new ResponseObject();
        try {
            logger.info("Check bucket: {}", bucket);
            if (minioClient.bucketExists(bucket)) {
                logger.info("Bucket found: {}", bucket);

                logger.info("Sending file to Bucket {}", bucket);
                sendFileAmazon(fileName, file);
                responseObject.setMessage("File " + fileName + " uploaded");
                responseObject.setFile(fileName);
                logger.info("Upload completed");
            } else {
                logger.info("Bucket not found, creating Bucket: {}", bucket);
                minioClient.makeBucket(bucket);
                logger.info("Bucket created: {}", bucket);
                logger.info("Sending file to Bucket {}", bucket);
                sendFileAmazon(fileName, file);
                responseObject.setMessage("File " + fileName + " uploaded");
                responseObject.setFile(fileName);
                logger.info("Upload completed");
            }

        }catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException
                | XmlPullParserException e) {
            logger.error("Error file upload: {}", e.getMessage());
            responseObject.setMessage("Error uploading file");
            responseObject.setFile(fileName);
        }
        return responseObject;
    }

    @Override
    public InputStream downloadFileAmazon(String fileName) {
        InputStream inputStream = null;
        try {
            minioClient.statObject(bucket, fileName);
            inputStream = minioClient.getObject(bucket, fileName);
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException
                | XmlPullParserException e) {
            logger.error("Error file download: {}", e.getMessage());
        }

        return inputStream;
    }

    @Override
    public ResponseObject deleteFileAmazon(String fileName){
        ResponseObject responseObject = new ResponseObject();
        try {
            minioClient.removeObject(bucket, fileName);
            responseObject.setFile(fileName);
            responseObject.setMessage("File removed from Amazon");
            logger.info("File removed from Amazon");
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException
                | XmlPullParserException e) {
            logger.error("Cannot delete the file from Amazon: {}", e.getMessage());
        }
        return responseObject;
    }

    private void sendFileAmazon(String fileName, MultipartFile file){
        try {
            minioClient.putObject(bucket, fileName, convertToInputStream(file), file.getSize(), "application/octet-stream");
        }catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException
                | XmlPullParserException e) {
            logger.error("Error file upload: {}", e.getMessage());
        }
    }

    private InputStream convertToInputStream(MultipartFile file){
        try {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            return inputStream;
        } catch(IOException e){
            logger.error("Fail to convert file to InputStream: {}", e.getMessage());
            return null;
        }
    }

}
