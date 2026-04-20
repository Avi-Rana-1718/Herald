package com.notification.herald.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUtilsTest {

    @Test
    void generateRequestId_shouldNotBeNullOrEmpty() {
        String id = RequestUtils.generateRequestId();
        assertThat(id).isNotNull().isNotEmpty();
    }

    @Test
    void generateRequestId_shouldContainHyphenSeparatingUuidAndTimestamp() {
        String id = RequestUtils.generateRequestId();
        // UUID (5 hyphen-separated segments) + "-" + timestamp = at least 6 segments total
        String[] parts = id.split("-");
        assertThat(parts.length).isGreaterThanOrEqualTo(6);
    }

    @Test
    void generateRequestId_lastSegmentShouldBeNumericTimestamp() {
        String id = RequestUtils.generateRequestId();
        String[] parts = id.split("-");
        String timestamp = parts[parts.length - 1];
        assertThat(timestamp).matches("\\d+");
    }

    @Test
    void generateRequestId_shouldBeUniqueAcrossMultipleCalls() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            ids.add(RequestUtils.generateRequestId());
        }
        assertThat(ids).hasSize(100);
    }
}
