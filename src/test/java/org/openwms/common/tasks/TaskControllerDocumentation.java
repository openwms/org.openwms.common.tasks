/*
 * Copyright 2005-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A TaskControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@TasksApplicationTest
class TaskControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void shall_findNothing() throws Exception {
        mockMvc
                .perform(
                        get("/tasks")
                )
                .andExpect(status().isNoContent())
                .andDo(document("tasks-findAll-empty", preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @Sql(scripts = "classpath:test.sql")
    void shall_findAll() throws Exception {
        mockMvc
                .perform(
                        get("/tasks")
                )
                .andExpect(status().isOk())
                .andDo(document("tasks-findAll", preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @Sql(scripts = "classpath:test.sql")
    void shall_findOne() throws Exception {
        mockMvc
                .perform(
                        get("/tasks/1000")
                )
                .andExpect(status().isOk())
                .andDo(document("tasks-findOne", preprocessResponse(prettyPrint())))
        ;
    }
}
