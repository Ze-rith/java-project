package kr.hs.dgsw.springproject.domain.user.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.springproject.domain.user.domain.User;
import kr.hs.dgsw.springproject.domain.user.dto.request.CreateUserRequest;
import kr.hs.dgsw.springproject.domain.user.dto.response.CreateUserResponse;
import kr.hs.dgsw.springproject.domain.user.dto.response.UserFindByIdResponse;
import kr.hs.dgsw.springproject.domain.user.service.UserService;
import kr.hs.dgsw.springproject.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ApiResponse<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = userService.create(request);

        return ApiResponse.created(CreateUserResponse.of(user), "유저가 생성되었습니다.");
    }

    @GetMapping("/{id}")
    public ApiResponse<UserFindByIdResponse> findById(@PathVariable("id") Long id) {
        User user = userService.findById(id);

        return ApiResponse.ok(UserFindByIdResponse.of(user), "유저 정보를 조회했습니다.");
    }
}
