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
package org.openwms.common.tasks.events;

import org.openwms.common.tasks.impl.TaskEO;
import org.openwms.core.event.RootApplicationEvent;

/**
 * A TaskEvent.
 *
 * @author Heiko Scherrer
 */
public class TaskEvent extends RootApplicationEvent {

    private Type type;

    public enum Type {
        CREATED, STARTED, PAUSED, RESUMED, FINISHED
    }

    public TaskEvent(TaskEO source, Type type) {
        super(source);
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        this.type = type;
    }

    @Override
    public TaskEO getSource() {
        return (TaskEO) super.getSource();
    }

    public Type getType() {
        return type;
    }
}
