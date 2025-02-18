package com.bookcatalog.backend.controller;

import com.bookcatalog.backend.exceptions.UserNotFoundException;
import com.bookcatalog.backend.model.dto.RegisterUserDto;
import com.bookcatalog.backend.model.dto.UserDto;
import com.bookcatalog.backend.service.AuthenticationService;
import com.bookcatalog.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.bookcatalog.backend.model.User;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER') or hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> allUsers()  {

        Page<UserDto> users = userService.getAllUsers(0, 10).getBody();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{type}/{identifier}")
    @PreAuthorize("hasRole('SUPER') or hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable String type, @PathVariable String identifier) {

        try {

            Optional<User> userOpt = userService.getUserByIdentifier(identifier, type);

            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            UserDto userDto = new UserDto(user);

            return ResponseEntity.ok(userDto);

        } catch (UserNotFoundException | IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/new", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createUser(@RequestBody RegisterUserDto registerUserDto) {

        return userService.createUser(registerUserDto);
    }

    @DeleteMapping("/{type}/{identifier}")
    public ResponseEntity<Void> deleteUser(@PathVariable String type, @PathVariable String identifier) throws IOException {

        return userService.deleteUser(identifier, type);
    }

    @PutMapping("{type}/{identifier}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String type, @PathVariable String identifier,@RequestBody UserDto input) throws IOException {

        return userService.updateUser(identifier, type, input);
    }
}
