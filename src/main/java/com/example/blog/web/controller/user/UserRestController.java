package com.example.blog.web.controller.user;

import com.example.blog.api.UsersApi;
import com.example.blog.model.BadRequest;
import com.example.blog.model.UserDTO;
import com.example.blog.model.UserForm;
import com.example.blog.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserRestController implements UsersApi {

    private final UserService userService;
    private final DuplicateUsernameValidator duplicateUsernameValidator;

    @InitBinder
    public void initBinder(DataBinder dataBinder) {
        dataBinder.addValidators(duplicateUsernameValidator);
    }

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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequest> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ){
        var body = new BadRequest();
        BeanUtils.copyProperties(e.getBody(), body);
        return ResponseEntity
                .badRequest()
                .body(body);
    }
}
