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
package org.openwms.common.tasks.events;

import org.ameba.annotation.Measured;
import org.ameba.app.SpringProfiles;
import org.openwms.common.tasks.impl.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * A TaskEventPropagator.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class TaskEventPropagator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskEventPropagator.class);
    private final TaskMapper mapper;
    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;

    TaskEventPropagator(AmqpTemplate amqpTemplate, @Value("${owms.tasks.exchange-name}") String exchangeName, TaskMapper mapper) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
        this.mapper = mapper;
    }

    @Measured
    @TransactionalEventListener(fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onEvent(TaskEvent event) {
        switch(event.getType()) {
            case CREATED:
                LOGGER.info("Task created [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "task.event.created", mapper.convertToMO(event.getSource()));
                break;
            case STARTED:
                LOGGER.info("Task started [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "task.event.started", mapper.convertToMO(event.getSource()));
                break;
            case PAUSED:
                LOGGER.info("Task paused [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "task.event.paused", mapper.convertToMO(event.getSource()));
                break;
            case RESUMED:
                LOGGER.info("Task resumed [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "task.event.resumed", mapper.convertToMO(event.getSource()));
                break;
            case FINISHED:
                LOGGER.info("Task finished [{}]", event.getSource());
                amqpTemplate.convertAndSend(exchangeName, "task.event.finished", mapper.convertToMO(event.getSource()));
                break;
            default:
                LOGGER.warn("TaskEvent of type [{}] is not propagated", event.getType());
        }
    }
}
