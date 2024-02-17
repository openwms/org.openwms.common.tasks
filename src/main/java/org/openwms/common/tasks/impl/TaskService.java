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

import org.openwms.common.tasks.api.TaskVO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A TaskService.
 *
 * @author Heiko Scherrer
 */
public interface TaskService {

    List<TaskVO> findAll();

    TaskVO findByPKeyOrThrow(@NotEmpty String pKey);

    TaskVO create(@NotNull @Valid TaskVO task);

    TaskVO update(@NotNull TaskVO task);

    TaskVO start(@NotEmpty String pKey);

    TaskVO pause(@NotEmpty String pKey);

    TaskVO resume(@NotEmpty String pKey);

    TaskVO finish(@NotEmpty String pKey);

}
