package com.rtvnewsnetwork.file.fileupload.controller;

import com.rtvnewsnetwork.file.fileupload.model.S3Path;
import com.rtvnewsnetwork.file.fileupload.model.UploadedFile;
import com.rtvnewsnetwork.file.fileupload.model.VideoAndThumbnailResponse;
import com.rtvnewsnetwork.file.fileupload.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/file")
public class FileController {

    private final FileService awsS3Service;
    @Value("${rtv.s3.bucket}")
    private String bucket;

    public FileController(FileService awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @PostMapping
    public ResponseEntity<UploadedFile> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value="type" ,defaultValue = "MEDIA_IMAGES") S3Path path
    ) throws IOException {
        Long size = file.getSize();

        UploadedFile uploadedFile = awsS3Service.upload(file, path, bucket, size);
         return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON) // ensures correct content type
                .body(uploadedFile);
    }

    @PostMapping("/createThumbnail")
    public ResponseEntity<UploadedFile> createThumbnail(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") S3Path path
    ) throws Exception {
        Long size = file.getSize();

        UploadedFile thumbnail = awsS3Service.thumbnailCreation(file, path, bucket, size);
        return new ResponseEntity<>(thumbnail, HttpStatus.OK);
    }

    @PostMapping("/videoAndThumbnailUpload")
    public ResponseEntity<VideoAndThumbnailResponse> uploadVideoAndThumbnail(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "videoPath", defaultValue = "MEDIA_VIDEOS") S3Path videoPath,
            @RequestParam(value = "thumbnailPath", defaultValue = "MEDIA_IMAGES") S3Path thumbnailPath
    ) throws Exception {
        Long size = file.getSize();

        VideoAndThumbnailResponse response = awsS3Service.videoAndthumbnailUpload(file, videoPath, thumbnailPath, bucket, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
