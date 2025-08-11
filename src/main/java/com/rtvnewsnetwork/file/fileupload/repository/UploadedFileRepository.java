package com.rtvnewsnetwork.file.fileupload.repository;

import com.rtvnewsnetwork.file.fileupload.model.UploadedFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UploadedFileRepository extends MongoRepository<UploadedFile, String> {
    Optional<UploadedFile> findById(String fileId);
}
