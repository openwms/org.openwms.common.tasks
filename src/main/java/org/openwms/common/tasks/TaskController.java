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
package org.openwms.common.tasks;

import org.ameba.http.MeasuredRestController;
import org.openwms.common.tasks.api.TaskVO;
import org.openwms.common.tasks.impl.TaskService;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.openwms.common.tasks.api.TaskVO.MEDIA_TYPE;
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

    @GetMapping(value = "/tasks", produces = MEDIA_TYPE)
    public ResponseEntity<List<TaskVO>> findAll() {
        var result = taskService.findAll();
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        result.forEach(this::addLinks);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/tasks/{pKey}", produces = MEDIA_TYPE)
    public ResponseEntity<TaskVO> findByPKey(@PathVariable("pKey") String pKey) {
        var result = taskService.findByPKeyOrThrow(pKey);
        addLinks(result);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/tasks", consumes = MEDIA_TYPE, produces = MEDIA_TYPE)
    public ResponseEntity<TaskVO> create(@RequestBody TaskVO task, HttpServletRequest req) {
        var result = taskService.create(task);
        addLinks(result);
        return ResponseEntity.created(super.getLocationURIForCreatedResource(req, result.getpKey())).body(result);
    }

    private void addLinks(TaskVO vo) {
        vo.add(linkTo(methodOn(TaskController.class).findByPKey(vo.getpKey())).withSelfRel());
    }

    @PutMapping(value = "/tasks", consumes = MEDIA_TYPE, produces = MEDIA_TYPE)
    public ResponseEntity<TaskVO> update(@RequestBody TaskVO task) {
        var updated = taskService.update(task);
        addLinks(updated);
        return ResponseEntity.ok(updated);
    }

    @PostMapping(value = "/tasks/{pKey}/start", produces = MEDIA_TYPE)
    public ResponseEntity<TaskVO> start(@PathVariable("pKey") String pKey) {
        var task = taskService.start(pKey);
        addLinks(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping(value = "/tasks/{pKey}/pause", produces = MEDIA_TYPE)
    public ResponseEntity<TaskVO> pause(@PathVariable("pKey") String pKey) {
        var task = taskService.pause(pKey);
        addLinks(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping(value = "/tasks/{pKey}/resume", produces = MEDIA_TYPE)
    public ResponseEntity<TaskVO> resume(@PathVariable("pKey") String pKey) {
        var task = taskService.resume(pKey);
        addLinks(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping(value = "/tasks/{pKey}/finish", produces = MEDIA_TYPE)
    public ResponseEntity<TaskVO> finish(@PathVariable("pKey") String pKey) {
        var task = taskService.finish(pKey);
        addLinks(task);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(TaskController.class).findAll()).withRel("tasks-findall"),
                        linkTo(methodOn(TaskController.class).findByPKey("identifier")).withRel("tasks-findbypkey"),
                        linkTo(methodOn(TaskController.class).create(new TaskVO(), null)).withRel("tasks-create"),
                        linkTo(methodOn(TaskController.class).start("identifier")).withRel("tasks-start"),
                        linkTo(methodOn(TaskController.class).pause("identifier")).withRel("tasks-pause"),
                        linkTo(methodOn(TaskController.class).resume("identifier")).withRel("tasks-resume"),
                        linkTo(methodOn(TaskController.class).finish("identifier")).withRel("tasks-finish"),
                        linkTo(methodOn(TaskController.class).update(new TaskVO())).withRel("tasks-update")
                )
        );
    }
}
