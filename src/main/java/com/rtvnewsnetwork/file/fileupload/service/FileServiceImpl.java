package com.rtvnewsnetwork.file.fileupload.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.rtvnewsnetwork.file.common.util.ImageCompressor;
import com.rtvnewsnetwork.file.fileupload.model.S3Path;
import com.rtvnewsnetwork.file.fileupload.model.UploadedFile;
import com.rtvnewsnetwork.file.fileupload.model.VideoAndThumbnailResponse;
import com.rtvnewsnetwork.file.fileupload.repository.UploadedFileRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    private final AmazonS3 amazonS3;
    private final Integer maxBannerSizeBytes;
    private final UploadedFileRepository uploadedFileRepository;

    @Autowired
    public FileServiceImpl(AmazonS3 amazonS3,
                           @Value("${properties.banner.max_size}") Integer maxBannerSizeBytes,
                           UploadedFileRepository uploadedFileRepository) {
        this.amazonS3 = amazonS3;
        this.maxBannerSizeBytes = maxBannerSizeBytes;
        this.uploadedFileRepository = uploadedFileRepository;
    }

    @Override
    public UploadedFile upload(InputStream inputStream, String path, String bucket, Long size) {
        var metadata=new ObjectMetadata();
        metadata.setContentLength(size);
        return upload(inputStream, path, metadata, bucket);
    }

    @Override
    public UploadedFile upload(InputStream inputStream, String path, ObjectMetadata metadata, String bucket) {
        PutObjectResult result = amazonS3.putObject(bucket, path, inputStream, metadata);
        return this.saveEntry(path);
    }

    @Override
    public UploadedFile upload(MultipartFile multipartFile, S3Path path, String bucket, Long size) throws IOException {
        InputStream inputStream =
                (multipartFile.getSize() > maxBannerSizeBytes) ? ImageCompressor.compress(
                        multipartFile.getInputStream()) : multipartFile.getInputStream();
        return upload(inputStream, getFullPath(path, multipartFile.getOriginalFilename()), bucket,size);
    }

//    @Override
//    public UploadedFile upload(InputStream inputStream, S3Path path, String fileName, String bucket)
//            throws IOException {
//        return upload(inputStream, getFullPath(path, fileName), bucket);
//    }

    @Override
    public UploadedFile uploadWithExactName(InputStream in, S3Path s3Path, String fileName,
                                            ObjectMetadata metadata, String bucket)
            throws IOException {
        return upload(in, s3Path.getPath() + fileName, metadata, bucket);
    }

    @Override
    public UploadedFile saveEntry(UploadedFile uploadedFile) {
        return this.uploadedFileRepository.save(uploadedFile);
    }

    @Override
    public UploadedFile saveEntry(String relativePath) {
        return this.saveEntry(
                new UploadedFile(null, new UploadedFile.RelativePath(relativePath), Instant.now()));
    }

    @Override
    public Optional<UploadedFile> getEntry(String id) {
        return this.uploadedFileRepository.findById(id);
    }

    private String getFullPath(S3Path path, String fileName) {
        return path.getPath() + this.randomFileName(fileName);
    }

    private String randomFileName(String fileName) {
        return RandomStringUtils.randomAlphabetic(32) + "." + FilenameUtils.getExtension(fileName);
    }

    @Override
    public boolean isSupportedExtension(String extension) {
        return extension != null && (
                extension.equals("png")
                        || extension.equals("jpg")
                        || extension.equals("jpeg"));
    }

    @Override
    public boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }

    /**
     * below function is used to create thumbnail from video file and upload that thumbnail is S3
     **/
    @Override
    public UploadedFile thumbnailCreation(MultipartFile multipartFile, S3Path s3Path, String bucket,Long size) throws Exception {
        InputStream thumbnail = createThumbnail(multipartFile.getInputStream());

        File tempFile = File.createTempFile("thumbnail", ".tmp");
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            thumbnail.transferTo(outputStream);
        }

        // Get the size of the temporary file
        size = tempFile.length();

        // Upload the file using FileInputStream
        try (InputStream thumbnailInputStream = new FileInputStream(tempFile)) {
            String path = s3Path.getPath() + randomName();
            return upload(thumbnailInputStream, path, bucket, size);
        } finally {
            // Clean up the temporary file
            boolean flag= tempFile.delete();
            System.out.println("file is deleted "+flag);
        }
    }

    public InputStream createThumbnail(InputStream videoStream) throws IOException {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoStream);
        frameGrabber.start();

        Frame frame = null;
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = null;

        int count = 0;
        while (count < 25) {
            frame = frameGrabber.grabImage();
            count++;
        }
        bufferedImage = converter.convert(frame);
        frameGrabber.stop();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private String randomName() {
        return RandomStringUtils.randomAlphanumeric(32) + ".jpg";
    }

    @Override
    public VideoAndThumbnailResponse videoAndthumbnailUpload(MultipartFile multipartFile, S3Path videoS3Path, S3Path thumbnailS3Path, String bucket, Long size) throws Exception {
        UploadedFile thumbnailResponse = thumbnailCreation(multipartFile, thumbnailS3Path, bucket,size);
        UploadedFile videoResponse = upload(multipartFile, videoS3Path, bucket,size);
        return new VideoAndThumbnailResponse(videoResponse, thumbnailResponse);
    }
}
