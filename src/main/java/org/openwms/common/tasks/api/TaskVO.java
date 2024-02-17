/*
 * Copyright 2005-2024 the original author or authors.
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
package org.openwms.common.tasks.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;

import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import java.util.Objects;

import static org.openwms.common.tasks.TimeProvider.DATE_TIME_WITH_TIMEZONE;

/**
 * A TaskVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskVO extends AbstractBase<TaskVO> {

    public static final String MEDIA_TYPE = "application/vnd.openwms.common.task-v1+json";

    @NotEmpty(groups = {ValidationGroups.Update.class})
    @JsonProperty("pKey")
    private String pKey;

    @JsonProperty("taskId")
    private String taskId;

    @JsonProperty("description")
    private String description;

    @NotEmpty(groups = {ValidationGroups.Create.class})
    @JsonProperty("type")
    private String type;

    @JsonProperty("state")
    private TaskState state;

    @JsonProperty("startedAt")
    @JsonFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime startedAt;

    @JsonProperty("finishedAt")
    @JsonFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime finishedAt;

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public ZonedDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(ZonedDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public ZonedDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(ZonedDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    /**
     * {@inheritDoc}
     *
     * All Fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskVO)) return false;
        if (!super.equals(o)) return false;
        TaskVO taskVO = (TaskVO) o;
        return Objects.equals(pKey, taskVO.pKey) && Objects.equals(taskId, taskVO.taskId) && Objects.equals(description, taskVO.description) && Objects.equals(type, taskVO.type) && state == taskVO.state && Objects.equals(startedAt, taskVO.startedAt) && Objects.equals(finishedAt, taskVO.finishedAt);
    }

    /**
     * {@inheritDoc}
     *
     * All Fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, taskId, description, type, state, startedAt, finishedAt);
    }
}
