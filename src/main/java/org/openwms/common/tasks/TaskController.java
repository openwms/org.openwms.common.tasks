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
package org.openwms.common.tasks;

import org.ameba.http.MeasuredRestController;
import org.openwms.common.tasks.api.TaskVO;
import org.openwms.common.tasks.impl.TaskService;
import org.openwms.core.http.AbstractWebController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A TaskController.
 *
 * @author Heiko Scherrer
 */
@Validated
@MeasuredRestController
public class TaskController extends AbstractWebController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(value = "/tasks", produces = "application/vnd.openwms.common.task-v1+json")
    public ResponseEntity<List<TaskVO>> findAll() {
        var result = taskService.findAll();
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        result.forEach(this::addLinks);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/tasks/{pKey}", produces = "application/vnd.openwms.common.task-v1+json")
    public ResponseEntity<TaskVO> findByPKey(@PathVariable("pKey") String pKey) {
        var result = taskService.findByPKeyOrThrow(pKey);
        addLinks(result);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/tasks", consumes = "application/vnd.openwms.common.task-v1+json",
            produces = "application/vnd.openwms.common.task-v1+json")
    public ResponseEntity<TaskVO> create(@RequestBody TaskVO task, HttpServletRequest req) {
        var result = taskService.create(task);
        addLinks(result);
        return ResponseEntity.created(super.getLocationURIForCreatedResource(req, result.getpKey())).body(result);
    }

    private void addLinks(TaskVO vo) {
        vo.add(linkTo(methodOn(TaskController.class).findByPKey(vo.getpKey())).withSelfRel());
    }

    @PutMapping(value = "/tasks", consumes = "application/vnd.openwms.common.task-v1+json",
            produces = "application/vnd.openwms.common.task-v1+json")
    public ResponseEntity<TaskVO> update(@RequestBody TaskVO task) {

        return ResponseEntity.ok(new TaskVO());
    }

    @PostMapping(value = "/tasks/{pKey}/start", produces = "application/vnd.openwms.common.task-v1+json")
    public ResponseEntity<TaskVO> start(@PathVariable("pKey") String pKey) {
        var task = taskService.start(pKey);
        return ResponseEntity.ok(task);
    }

    @PostMapping(value = "/tasks/{pKey}/pause", produces = "application/vnd.openwms.common.task-v1+json")
    public ResponseEntity<TaskVO> pause(@PathVariable("pKey") String pKey) {
        var task = taskService.pause(pKey);
        return ResponseEntity.ok(task);
    }

    @PostMapping(value = "/tasks/{pKey}/resume", produces = "application/vnd.openwms.common.task-v1+json")
    public ResponseEntity<TaskVO> resume(@PathVariable("pKey") String pKey) {
        var task = taskService.resume(pKey);
        return ResponseEntity.ok(task);
    }

    @PostMapping(value = "/tasks/{pKey}/finish", produces = "application/vnd.openwms.common.task-v1+json")
    public ResponseEntity<TaskVO> finish(@PathVariable("pKey") String pKey) {
        var task = taskService.finish(pKey);
        return ResponseEntity.ok(task);
    }
}
