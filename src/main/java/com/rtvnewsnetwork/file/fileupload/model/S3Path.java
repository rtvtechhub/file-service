package com.rtvnewsnetwork.file.fileupload.model;

public enum S3Path {
    MEDIA_IMAGES("images/"),
    MEDIA_VIDEOS("videos/"),
    DOCUMENTS("documents/"),
    TRAINING_VIDEOS("training_videos/"),
    TRAINING_IMAGES("training_images/"),
    USER_IMAGES("user_images");
    private final String path;

    S3Path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}