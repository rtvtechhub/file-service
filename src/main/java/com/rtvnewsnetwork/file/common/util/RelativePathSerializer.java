package com.rtvnewsnetwork.file.common.util;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.rtvnewsnetwork.file.fileupload.model.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@JsonComponent
public class RelativePathSerializer extends JsonSerializer<UploadedFile.RelativePath> {

    private final String cloudfrontUrl;

    public RelativePathSerializer(@Value("${cloudfront.url}") String cloudfrontUrl) {
        this.cloudfrontUrl = cloudfrontUrl;
    }

    @Override
    public void serialize(UploadedFile.RelativePath value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeString(cloudfrontUrl + value.getPath());
    }
}
