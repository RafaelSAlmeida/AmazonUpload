package br.com.controller;

import br.com.dto.ResponseObject;
import br.com.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/amazon-upload")
public class AmazonUploadController {

    private Logger logger = LoggerFactory.getLogger(AmazonUploadController.class);

    @Autowired
    private MinioService minioService;

    @RequestMapping(value="/putFile", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseObject putFile(@RequestPart(value="fileName", required=true) String fileName, @RequestPart(value="file", required=true) MultipartFile file) throws IOException {
        ResponseObject responseObject = minioService.uploadFileAmazon(fileName, file);
        return responseObject;
    }

    @RequestMapping(value="/getFile", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseObject getFile(@RequestParam("fileName") String fileName) throws IOException {
        ResponseObject responseObject = new ResponseObject();
        InputStream inputStream = minioService.downloadFileAmazon(fileName);
        if(inputStream == null){
            responseObject.setMessage("File " + fileName +" not found");
        }
        else{
            responseObject.setBytes(IOUtils.toByteArray(inputStream));
        }
        return responseObject;
    }

    @RequestMapping(value="/delFile", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseObject delFile(@RequestParam("fileName") String fileName) throws IOException {
        ResponseObject responseObject = minioService.deleteFileAmazon(fileName);
        return responseObject;
    }

}
