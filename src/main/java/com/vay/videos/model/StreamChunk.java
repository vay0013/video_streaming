package com.vay.videos.model;

import java.util.Arrays;
import java.util.Objects;

public record StreamChunk(FileMetadata fileMetadata, byte[] chunk) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StreamChunk that)) return false;
        return Objects.equals(fileMetadata, that.fileMetadata) && Objects.deepEquals(chunk, that.chunk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileMetadata, Arrays.hashCode(chunk));
    }
}
