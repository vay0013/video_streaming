package com.vay.videos.model;

public record StreamRange(long rangeStart, long rangeEnd) {

    public long getRangeEnd(long fileSize) {
        return Math.min(rangeEnd, fileSize - 1);
    }

    public static StreamRange parseHttpRangeString(String httpRangeString, long defaultChunkSize) {
        if (httpRangeString == null) {
            return new StreamRange(0, defaultChunkSize);
        }

        int dashIndex = httpRangeString.indexOf("-");
        if (dashIndex == -1 || dashIndex <= 6) { // maybe some problem
            throw new IllegalArgumentException("Некорректный формат HTTP Range: " + httpRangeString);
        }

        long startRange = Long.parseLong(httpRangeString.substring(6, dashIndex));
        String endRangeString = httpRangeString.substring(dashIndex + 1);

        if (endRangeString.isEmpty()) {
            return new StreamRange(startRange, startRange + defaultChunkSize);
        }

        long endRange = Long.parseLong(endRangeString);
        return new StreamRange(startRange, endRange);
    }
}