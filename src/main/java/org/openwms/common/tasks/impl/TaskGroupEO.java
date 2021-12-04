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

import org.ameba.integration.jpa.ApplicationEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
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
}
