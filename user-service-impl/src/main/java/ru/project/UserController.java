package ru.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создать нового пользователя", description = "Создаёт пользователя и возвращает его с HATEOAS-ссылками")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> createUser(@RequestBody UserDto userDto) {
        UserDto created = userService.create(userDto);

        EntityModel<UserDto> resource = EntityModel.of(created);
        String uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUriString();

        resource.add(Link.of(uri).withSelfRel());
        resource.add(Link.of("/api/users").withRel("users"));

        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(UserController.class).getUserById(created.getId())).toUri())
                .body(resource);
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> getUserById(@PathVariable Long id) {
        UserDto user = userService.getById(id);

        EntityModel<UserDto> resource = EntityModel.of(user);
        resource.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getUserById(id)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers()).withRel("users"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Получить список всех пользователей")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAll();

        List<EntityModel<UserDto>> resources = users.stream()
                .map(user -> EntityModel.of(user,
                        WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(UserController.class)
                                        .getUserById(user.getId())).withSelfRel()))
                .toList();

        CollectionModel<EntityModel<UserDto>> collection = CollectionModel.of(resources);
        collection.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Обновить пользователя")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updated = userService.update(id, userDto);

        EntityModel<UserDto> resource = EntityModel.of(updated);
        resource.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getUserById(id)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getAllUsers()).withRel("users"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Удалить пользователя")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}