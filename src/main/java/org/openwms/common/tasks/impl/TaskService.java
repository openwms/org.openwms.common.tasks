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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.openwms.common.tasks.api.TaskVO;

import java.util.List;

/**
 * A TaskService.
 *
 * @author Heiko Scherrer
 */
public interface TaskService {

    List<TaskVO> findAll();

    TaskVO findByPKeyOrThrow(@NotBlank String pKey);

    TaskVO create(@NotNull @Valid TaskVO task);

    TaskVO update(@NotNull TaskVO task);

    TaskVO start(@NotBlank String pKey);

    TaskVO pause(@NotBlank String pKey);

    TaskVO resume(@NotBlank String pKey);

    TaskVO finish(@NotBlank String pKey);

}
