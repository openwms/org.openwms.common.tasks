/*
 * Copyright 2005-2025 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A TaskGroupVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskGroupVO extends AbstractBase<TaskGroupVO> {

    /** The name of the TaskGroup. */
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tasks")
    private Set<TaskVO> tasks = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TaskVO> getTasks() {
        return tasks;
    }

    public void setTasks(Set<TaskVO> tasks) {
        this.tasks = tasks;
    }

    /**
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskGroupVO)) return false;
        if (!super.equals(o)) return false;
        TaskGroupVO that = (TaskGroupVO) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(tasks, that.tasks);
    }

    /**
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description, tasks);
    }
}
