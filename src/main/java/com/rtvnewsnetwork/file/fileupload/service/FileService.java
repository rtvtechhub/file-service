package com.rtvnewsnetwork.file.fileupload.service;


import com.amazonaws.services.s3.model.ObjectMetadata;
import com.rtvnewsnetwork.file.fileupload.model.S3Path;
import com.rtvnewsnetwork.file.fileupload.model.UploadedFile;
import com.rtvnewsnetwork.file.fileupload.model.VideoAndThumbnailResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface FileService {

    UploadedFile upload(InputStream inputStream, String path, String bucket, Long size);

    UploadedFile upload(InputStream inputStream, String path, ObjectMetadata metadata, String bucket);

    UploadedFile upload(MultipartFile multipartFile, S3Path path, String bucket, Long size) throws IOException;

    UploadedFile uploadWithExactName(InputStream in, S3Path s3Path, String fileName,
                                     ObjectMetadata metadata, String bucket)
            throws IOException;

    UploadedFile saveEntry(UploadedFile uploadedFile);

    UploadedFile saveEntry(String relativePath);

    Optional<UploadedFile> getEntry(String id);

    // UploadedFile upload(InputStream in, S3Path voucherLogo, String fileName, String bucket) throws IOException;

    boolean isSupportedExtension(String extension);

    boolean isSupportedContentType(String contentType);

    UploadedFile thumbnailCreation(MultipartFile multipartFile, S3Path path, String bucket,Long size) throws Exception;

    VideoAndThumbnailResponse videoAndthumbnailUpload(MultipartFile multipartFile, S3Path videoS3Path, S3Path thumbnailS3Path, String bucket, Long size) throws Exception;
}

