/*
 * Copyright 2005-2022 the original author or authors.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.tasks.api.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.openwms.common.tasks.api.TaskVO.MEDIA_TYPE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @Autowired
    private ObjectMapper om;
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
    @SqlGroup({
            @Sql(scripts = "classpath:delete-data.sql"),
            @Sql(scripts = "classpath:test.sql")
    })
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
    @SqlGroup({
            @Sql(scripts = "classpath:delete-data.sql"),
            @Sql(scripts = "classpath:test.sql")
    })
    void shall_findOne() throws Exception {
        mockMvc
                .perform(
                        get("/tasks/1000")
                )
                .andExpect(status().isOk())
                .andDo(document("tasks-findOne",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("links[]").description("Hypermedia links to the resource itself and others"),
                                fieldWithPath("links[].*").ignored(),
                                fieldWithPath("pKey").description("The persistent unique key"),
                                fieldWithPath("taskId").description("The unique business key"),
                                fieldWithPath("description").description("The descriptive text of the task"),
                                fieldWithPath("type").description("The task's type must be supported by the processing engine"),
                                fieldWithPath("state").description("A lifecycle state the task resides in"),
                                fieldWithPath("startedAt").optional().description("When the task has been started"),
                                fieldWithPath("finishedAt").optional().description("When the task has been ended")
                        )
                ))
        ;

        mockMvc
                .perform(
                        get("/tasks/UNKNOWN")
                )
                .andExpect(status().isNotFound())
                .andDo(document("tasks-findOne-404", preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    void shall_create() throws Exception {
        var vo = new TaskVO();
        vo.setType("USERTASK");
        vo.setDescription("Get a coffee");
        mockMvc
                .perform(
                        post("/tasks")
                                .contentType(MEDIA_TYPE)
                                .content(om.writeValueAsString(vo))
                )
                .andExpect(status().isCreated())
                .andDo(document("tasks-create",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("links[]").ignored(),
                                fieldWithPath("type").description("The type of task is a mandatory fields at creation"),
                                fieldWithPath("description").description("(Optional) descriptive text")
                        ),
                        responseFields(
                                fieldWithPath("links[]").description("Hypermedia links to the resource itself and others"),
                                fieldWithPath("links[].*").ignored(),
                                fieldWithPath("pKey").description("The persistent unique key"),
                                fieldWithPath("taskId").description("The unique business key"),
                                fieldWithPath("description").description("The descriptive text of the task"),
                                fieldWithPath("type").description("The task's type must be supported by the processing engine"),
                                fieldWithPath("state").description("A lifecycle state the task resides in")
                        )
                ))
                .andExpect(jsonPath("$.links.length()", is(1)))
                .andExpect(jsonPath("$.pKey").exists())
                .andExpect(jsonPath("$.taskId", is("T0000000001")))
                .andExpect(jsonPath("$.description", is("Get a coffee")))
                .andExpect(jsonPath("$.type", is("USERTASK")))
                .andExpect(jsonPath("$.state", is("CREATED")))
        ;
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:delete-data.sql"),
            @Sql(scripts = "classpath:test.sql")
    })
    void shall_update() throws Exception {
        var vo = new TaskVO();
        vo.setpKey("1000");
        vo.setDescription("Take a nap");
        mockMvc
                .perform(
                        put("/tasks")
                                .contentType(MEDIA_TYPE)
                                .content(om.writeValueAsString(vo))
                )
                .andExpect(status().isOk())
                .andDo(document("tasks-update",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("links[]").ignored(),
                                fieldWithPath("pKey").description("Mandatory to provide the pKey"),
                                fieldWithPath("description").description("In this example the description is updated")
                        )
                ))
                .andExpect(jsonPath("$.links.length()", is(1)))
                .andExpect(jsonPath("$.pKey").exists())
                .andExpect(jsonPath("$.taskId", is("T0000000001")))
                .andExpect(jsonPath("$.description", is("Take a nap")))
                .andExpect(jsonPath("$.type", is("MANUAL")))
                .andExpect(jsonPath("$.state", is("CREATED")))
        ;
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:delete-data.sql"),
            @Sql(scripts = "classpath:test.sql")
    })
    void shall_start() throws Exception {
        mockMvc
                .perform(
                        post("/tasks/1001/start")
                )
                .andExpect(status().isOk())
                .andDo(document("tasks-start",
                        preprocessResponse(prettyPrint())
                ))
                .andExpect(jsonPath("$.links.length()", is(1)))
                .andExpect(jsonPath("$.pKey").exists())
                .andExpect(jsonPath("$.taskId", is("T0000000002")))
                .andExpect(jsonPath("$.type", is("MANUAL")))
                .andExpect(jsonPath("$.state", is("ACTIVE")))
                .andExpect(jsonPath("$.startedAt").exists())
                .andExpect(jsonPath("$.finishedAt").doesNotExist())
        ;
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:delete-data.sql"),
            @Sql(scripts = "classpath:test.sql")
    })
    void shall_pause() throws Exception {
        mockMvc
                .perform(
                        post("/tasks/1002/pause")
                )
                .andExpect(status().isOk())
                .andDo(document("tasks-pause",
                        preprocessResponse(prettyPrint())
                ))
                .andExpect(jsonPath("$.links.length()", is(1)))
                .andExpect(jsonPath("$.pKey").exists())
                .andExpect(jsonPath("$.taskId", is("T0000000003")))
                .andExpect(jsonPath("$.type", is("MANUAL")))
                .andExpect(jsonPath("$.state", is("PAUSED")))
                .andExpect(jsonPath("$.startedAt").exists())
                .andExpect(jsonPath("$.finishedAt").doesNotExist())
        ;
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:delete-data.sql"),
            @Sql(scripts = "classpath:test.sql")
    })
    void shall_resume() throws Exception {
        mockMvc
                .perform(
                        post("/tasks/1003/resume")
                )
                .andExpect(status().isOk())
                .andDo(document("tasks-resume",
                        preprocessResponse(prettyPrint())
                ))
                .andExpect(jsonPath("$.links.length()", is(1)))
                .andExpect(jsonPath("$.pKey").exists())
                .andExpect(jsonPath("$.taskId", is("T0000000004")))
                .andExpect(jsonPath("$.type", is("MANUAL")))
                .andExpect(jsonPath("$.state", is("ACTIVE")))
                .andExpect(jsonPath("$.startedAt").exists())
                .andExpect(jsonPath("$.finishedAt").doesNotExist())
        ;
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "classpath:delete-data.sql"),
            @Sql(scripts = "classpath:test.sql")
    })
    void shall_finish() throws Exception {
        mockMvc
                .perform(
                        post("/tasks/1002/finish")
                )
                .andExpect(status().isOk())
                .andDo(document("tasks-finish",
                        preprocessResponse(prettyPrint())
                ))
                .andExpect(jsonPath("$.links.length()", is(1)))
                .andExpect(jsonPath("$.pKey").exists())
                .andExpect(jsonPath("$.taskId", is("T0000000003")))
                .andExpect(jsonPath("$.type", is("MANUAL")))
                .andExpect(jsonPath("$.state", is("FINISHED")))
                .andExpect(jsonPath("$.startedAt").exists())
                .andExpect(jsonPath("$.finishedAt").exists())
        ;
    }

    @Test
    void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get("/tasks/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.length()", is(8)))
                .andDo(document("tasks-index", preprocessResponse(prettyPrint())))
        ;
    }
}
