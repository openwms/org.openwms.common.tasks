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
package org.openwms.common.tasks.impl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.ameba.integration.jpa.ApplicationEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A TaskGroupEO.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "TSK_TASK_GROUP")
public class TaskGroupEO extends ApplicationEntity implements Serializable {

    @NotNull
    @Column(name = "C_NAME", nullable = false)
    private String name;

    @Column(name = "C_DESCRIPTION")
    private String description;

    @OneToMany(mappedBy = "taskGroup")
    private Set<TaskEO> tasks = new HashSet<>();

    /**
     * {@inheritDoc}
     *
     * Only name and description.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        var that = (TaskGroupEO) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    /**
     * {@inheritDoc}
     *
     * Only name and description.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description);
    }
}
