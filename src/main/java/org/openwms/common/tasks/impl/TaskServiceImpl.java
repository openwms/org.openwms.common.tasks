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
package org.openwms.common.tasks.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.BusinessRuntimeException;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.i18n.Translator;
import org.openwms.common.tasks.TimeProvider;
import org.openwms.common.tasks.api.TaskState;
import org.openwms.common.tasks.api.TaskVO;
import org.openwms.common.tasks.api.ValidationGroups;
import org.openwms.common.tasks.events.TaskEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import static java.lang.String.format;
import static org.openwms.common.tasks.TaskMessageCodes.TASK_ALREADY_FINISHED;
import static org.openwms.common.tasks.TaskMessageCodes.TASK_ALREADY_PAUSED;
import static org.openwms.common.tasks.TaskMessageCodes.TASK_ALREADY_STARTED;
import static org.openwms.common.tasks.TaskMessageCodes.TASK_EXISTS;
import static org.openwms.common.tasks.TaskMessageCodes.TASK_IS_NOT_PAUSED;
import static org.openwms.common.tasks.TaskMessageCodes.TASK_IS_PAUSED;
import static org.openwms.common.tasks.TaskMessageCodes.TASK_NOT_ACTIVE;
import static org.openwms.common.tasks.TaskMessageCodes.TASK_NOT_FOUND;

/**
 * A TaskServiceImpl.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class TaskServiceImpl implements TaskService {

    private final TimeProvider timeProvider = ServiceLoader.load(TimeProvider.class).iterator().next();
    private final ApplicationEventPublisher eventPublisher;
    private final Translator translator;
    private final TaskMapper mapper;
    private final TaskRepository taskRepository;

    TaskServiceImpl(ApplicationEventPublisher eventPublisher, Translator translator, TaskMapper mapper, TaskRepository taskRepository) {
        this.eventPublisher = eventPublisher;
        this.translator = translator;
        this.mapper = mapper;
        this.taskRepository = taskRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public List<TaskVO> findAll() {
        var result = taskRepository.findAll(Sort.by(Sort.Direction.ASC, "taskId"));
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return mapper.convertToVO(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TaskVO findByPKeyOrThrow(@NotEmpty String pKey) {
        return mapper.convertToVO(findInternal(pKey));
    }

    private TaskEO findInternal(String pKey) {
        var taskOpt = taskRepository.findBypKey(pKey);
        if (taskOpt.isEmpty()) {
            throw new NotFoundException(translator, TASK_NOT_FOUND , new String[]{pKey}, pKey);
        }
        return taskOpt.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Validated(ValidationGroups.Create.class)
    public TaskVO create(@NotNull @Valid TaskVO task) {
        if (task.getpKey() != null && !task.getpKey().isEmpty()) {
            throw new ResourceExistsException(translator, TASK_EXISTS);
        }
        var eo = mapper.convertToEO(task);
        var pk = taskRepository.findLastPK(PageRequest.of(0, 1));
        eo.setTaskId(format("T%1$10s", ((pk == null || pk.isEmpty() ? 0 : pk.get(0)) + 1)).replace(' ', '0'));
        var created = taskRepository.save(eo);
        eventPublisher.publishEvent(new TaskEvent(created, TaskEvent.Type.CREATED));
        return mapper.convertToVO(created);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TaskVO update(@NotNull TaskVO task) {
        var existing = findInternal(task.getpKey());
        existing.setDescription(task.getDescription());
        return mapper.convertToVO(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TaskVO start(@NotEmpty String pKey) {
        var existing = findInternal(pKey);
        if (existing.getState() == TaskState.FINISHED) {
            throw new BusinessRuntimeException(translator, TASK_ALREADY_FINISHED, new String[]{pKey}, pKey);
        }
        if (existing.getState() == TaskState.ACTIVE) {
            throw new BusinessRuntimeException(translator, TASK_ALREADY_STARTED, new String[]{pKey}, pKey);
        }
        if (existing.getState() == TaskState.PAUSED) {
            throw new BusinessRuntimeException(translator, TASK_IS_PAUSED, new String[]{pKey}, pKey);
        }
        existing.setState(TaskState.ACTIVE);
        existing.setStartedAt(timeProvider.nowAsZonedDateTime());
        existing = taskRepository.save(existing);
        eventPublisher.publishEvent(new TaskEvent(existing, TaskEvent.Type.STARTED));
        return mapper.convertToVO(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TaskVO pause(@NotEmpty String pKey) {
        var existing = findInternal(pKey);
        if (existing.getState() == TaskState.FINISHED) {
            throw new BusinessRuntimeException(translator, TASK_ALREADY_FINISHED, new String[]{pKey}, pKey);
        }
        if (existing.getState() == TaskState.CREATED) {
            throw new BusinessRuntimeException(translator, TASK_NOT_ACTIVE, new String[]{pKey}, pKey);
        }
        if (existing.getState() != TaskState.ACTIVE) {
            throw new BusinessRuntimeException(translator, TASK_ALREADY_PAUSED, new String[]{pKey}, pKey);
        }
        existing.setState(TaskState.PAUSED);
        existing = taskRepository.save(existing);
        eventPublisher.publishEvent(new TaskEvent(existing, TaskEvent.Type.PAUSED));
        return mapper.convertToVO(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TaskVO resume(@NotEmpty String pKey) {
        var existing = findInternal(pKey);
        if (existing.getState() == TaskState.FINISHED) {
            throw new BusinessRuntimeException(translator, TASK_ALREADY_FINISHED, new String[]{pKey}, pKey);
        }
        if (existing.getState() == TaskState.CREATED) {
            throw new BusinessRuntimeException(translator, TASK_NOT_ACTIVE, new String[]{pKey}, pKey);
        }
        if (existing.getState() != TaskState.PAUSED) {
            throw new BusinessRuntimeException(translator, TASK_IS_NOT_PAUSED, new String[]{pKey}, pKey);
        }
        existing.setState(TaskState.ACTIVE);
        existing = taskRepository.save(existing);
        eventPublisher.publishEvent(new TaskEvent(existing, TaskEvent.Type.RESUMED));
        return mapper.convertToVO(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public TaskVO finish(@NotEmpty String pKey) {
        var existing = findInternal(pKey);
        if (existing.getState() != TaskState.FINISHED) {
            existing.setState(TaskState.FINISHED);
            existing.setFinishedAt(timeProvider.nowAsZonedDateTime());
            existing = taskRepository.save(existing);
            eventPublisher.publishEvent(new TaskEvent(existing, TaskEvent.Type.FINISHED));
        }
        return mapper.convertToVO(existing);
    }
}
