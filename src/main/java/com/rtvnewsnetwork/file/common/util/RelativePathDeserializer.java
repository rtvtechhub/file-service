package com.rtvnewsnetwork.file.common.util;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.rtvnewsnetwork.file.fileupload.model.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class RelativePathDeserializer extends JsonDeserializer<UploadedFile.RelativePath> {
    private final String cloudfrontUrl;

    public RelativePathDeserializer(@Value("${cloudfront.url}") String cloudfrontUrl) {
        this.cloudfrontUrl = cloudfrontUrl;
    }
    @Override
    public UploadedFile.RelativePath deserialize(JsonParser jsonParser,
                                                 DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        return new UploadedFile.RelativePath(jsonParser.getText().substring(cloudfrontUrl.length()));
    }
}

