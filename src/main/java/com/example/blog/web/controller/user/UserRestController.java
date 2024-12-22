package com.example.blog.web.controller.user;

import com.example.blog.api.UsersApi;
import com.example.blog.model.UserDTO;
import com.example.blog.model.UserForm;
import com.example.blog.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserRestController implements UsersApi {

    private final UserService userService;
    // GET /users/me
    @GetMapping("/users/me")
    public ResponseEntity<String> me(Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }

    // POST
    @Override
    public ResponseEntity<UserDTO> createUser(UserForm userForm) {
        var newUser = userService.register(userForm.getUsername(), userForm.getPassword());
        var location = UriComponentsBuilder.fromPath("/users/{id}")
                .buildAndExpand(newUser.getId())
                .toUri();
        var dto = new UserDTO();
                dto.setId(newUser.getId());
                dto.setUsername(newUser.getUsername());
        return ResponseEntity
                .created(location)
                .body(dto);
    }
}
