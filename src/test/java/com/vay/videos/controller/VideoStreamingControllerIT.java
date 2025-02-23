package com.vay.videos.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class VideoStreamingControllerIT {

    @Autowired
    MockMvc mockMvc;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    @Sql("/sql/metadata.sql")
    @Commit
    void getVideoList() throws Exception {
        mockMvc.perform(get("/api/v1/videos/"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {"id": 2, "name": "aaa", "uuid": "323e83a9-c366-439e-936c-91885c68f504", "content_type": "video/mp4", "content_size": 107789535, "uploaded_at": "2025-02-21 20:15:50.263413"}
                                ]
                                """)

                );
    }
}
//                        content().json("""
//                                [
//                                    {"id": 1, "name":"video#1", "uuid":"9f86af4d-714d-44f5-9a56-3d69e8a4073f", "contentType":"video/mp4", "contentSize":10000, "uploadedAt":"2025-02-21 20:15:50.263413"},
//                                    {"id": 2, "name":"video#2", "uuid":"8f86af4d-714d-44f5-9a56-3d69e8a4073f", "contentType":"video/mp4", "contentSize":10000, "uploadedAt":"2025-02-21 20:15:50.263413"},
//                                    {"id": 3, "name":"video#3", "uuid":"7f86af4d-714d-44f5-9a56-3d69e8a4073f", "contentType":"video/mp4", "contentSize":10000, "uploadedAt":"2025-02-21 20:15:50.263413"},
//                                    {"id": 4, "name":"video#4", "uuid":"6f86af4d-714d-44f5-9a56-3d69e8a4073f", "contentType":"video/mp4", "contentSize":10000, "uploadedAt":"2025-02-21 20:15:50.263413"},
//                                ]
//                                """)