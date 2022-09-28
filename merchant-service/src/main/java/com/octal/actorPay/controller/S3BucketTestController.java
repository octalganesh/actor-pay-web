package com.octal.actorPay.controller;

import com.octal.actorPay.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/s3")
public class S3BucketTestController {

    private UploadService  uploadService;

    public S3BucketTestController(UploadService uploadService) {
        this.uploadService=uploadService;
    }

    @Secured("ROLE_S3BUCKET_FILE_DELETE")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("key") String key){
        try {
            uploadService.deleteFile(key);
            return new ResponseEntity<>("File Deleted", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("File is not Deleted", HttpStatus.BAD_REQUEST);
        }
    }
}
