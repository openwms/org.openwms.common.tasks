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
package org.openwms.common.tasks.impl;

import org.ameba.integration.jpa.ApplicationEntity;
import org.openwms.common.tasks.api.TaskState;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;

import static org.openwms.common.tasks.TimeProvider.DATE_TIME_WITH_TIMEZONE;

/**
 * A TaskEO.
 *
 * @author Heiko Scherrer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "TSK_TASK", uniqueConstraints = @UniqueConstraint(name = "UC_TASK_ID", columnNames = "C_TASK_ID"))
public class TaskEO extends ApplicationEntity implements Serializable {

    @NotNull
    @Column(name = "C_TASK_ID", nullable = false)
    private String taskId;

    @Column(name = "C_DESCRIPTION")
    private String description;

    @NotNull
    @Column(name = "C_TYPE")
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "C_STATE")
    private TaskState state;

    @ManyToOne
    @JoinColumn(name = "C_PARENT_TASK", foreignKey = @ForeignKey(name = "FK_TSK_TSK_PARENT"))
    private TaskEO parentTask;

    @ManyToOne
    @JoinColumn(name = "C_TASK_GROUP", foreignKey = @ForeignKey(name = "FK_TSK_GRP"))
    private TaskGroupEO taskGroup;

    @Column(name = "C_ASSIGNED_TO")
    private String assignedTo;

    @Column(name = "C_ASSIGNED_AT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime assignedAt;

    @Column(name = "C_STARTED_AT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime startedAt;

    @Column(name = "C_FINISHED_AT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime finishedAt;

    @Override
    protected void onEntityPersist() {
        this.state = TaskState.CREATED;
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

    public TaskEO getParentTask() {
        return parentTask;
    }

    public void setParentTask(TaskEO parentTask) {
        this.parentTask = parentTask;
    }

    public TaskGroupEO getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(TaskGroupEO taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(ZonedDateTime assignedAt) {
        this.assignedAt = assignedAt;
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
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskEO)) return false;
        if (!super.equals(o)) return false;
        TaskEO taskEO = (TaskEO) o;
        return Objects.equals(taskId, taskEO.taskId) && Objects.equals(description, taskEO.description) && Objects.equals(type, taskEO.type) && state == taskEO.state && Objects.equals(parentTask, taskEO.parentTask) && Objects.equals(taskGroup, taskEO.taskGroup) && Objects.equals(startedAt, taskEO.startedAt) && Objects.equals(finishedAt, taskEO.finishedAt);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), taskId, description, type, state, parentTask, taskGroup, startedAt, finishedAt);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", TaskEO.class.getSimpleName() + "[", "]")
                .add("taskId='" + taskId + "'")
                .add("description='" + description + "'")
                .add("type='" + type + "'")
                .add("state=" + state)
                .add("parentTask=" + parentTask)
                .add("taskGroup=" + taskGroup)
                .add("startedAt=" + startedAt)
                .add("finishedAt=" + finishedAt)
                .toString();
    }
}
