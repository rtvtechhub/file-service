package com.rtvnewsnetwork.file.fileupload.model;


public class VideoAndThumbnailResponse {
    UploadedFile video;
    UploadedFile thumbnail;

    public VideoAndThumbnailResponse() {
    }

    public VideoAndThumbnailResponse(UploadedFile video, UploadedFile thumbnail) {
        this.video = video;
        this.thumbnail = thumbnail;
    }

    public UploadedFile getVideo() {
        return video;
    }

    public void setVideo(UploadedFile video) {
        this.video = video;
    }

    public UploadedFile getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(UploadedFile thumbnail) {
        this.thumbnail = thumbnail;
    }
}
