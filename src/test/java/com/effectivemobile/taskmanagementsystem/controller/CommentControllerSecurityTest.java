package com.effectivemobile.taskmanagementsystem.controller;

import com.effectivemobile.taskmanagementsystem.dto.request.comment.CommentDtoCreateRequest;
import com.effectivemobile.taskmanagementsystem.security.JwtService;
import com.effectivemobile.taskmanagementsystem.security.SecurityConfig;
import com.effectivemobile.taskmanagementsystem.security.filter.JwtAuthenticationFilter;
import com.effectivemobile.taskmanagementsystem.util.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CommentController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@DisplayName("Тест безопасности эндпоинтов контроллера для работы с комментами")
public class CommentControllerSecurityTest {
    private static final GrantedAuthority[] USER_ROLES =
            new GrantedAuthority[]{new SimpleGrantedAuthority(Role.ROLE_USER.name())};

    private static final GrantedAuthority[] ADMIN_ROLES =
            new GrantedAuthority[]{new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())};

    @Autowired
    private MockMvc mvc;

    @Autowired
    private static ObjectMapper mapper;

    @MockBean
    private CommentController commentController;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeAll
    public static void init() {
        mapper = new ObjectMapper();
    }

    /**
     * Для случаев, когда доступ в метод по url разрешен
     */
    @BeforeEach
    void stubbing() {
        doThrow(new ResponseStatusException(HttpStatus.CREATED)).when(commentController)
                .create(any(CommentDtoCreateRequest.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(commentController)
                .getAllByTaskId(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(commentController)
                .get(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(commentController)
                .delete(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(commentController)
                .update(anyLong(), anyString());
    }

    @DisplayName("should return expected status")
    @ParameterizedTest(name = "{0} {1} for user {4} should return {6} status")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String method, String url, Map<String, String> params, String content,
                                    String userName, GrantedAuthority[] roles, int status) throws Exception {

        MockHttpServletRequestBuilder request = method2RequestBuilder(method, url, params, content);

        if (nonNull(userName)) {
            request = request.with(user(userName).authorities(roles));
        }

        mvc.perform(request)
                .andExpect(status().is(status));
    }

    private MultiValueMap<String, String> convertToMultiValueMap(Map<String, String> params) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(params.size());
        for (String key : params.keySet()) {
            map.add(key, params.get(key));
        }
        return map;
    }

    private MockHttpServletRequestBuilder method2RequestBuilder(
            String method, String url, Map<String, String> params, String content) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap =
                Map.of("get", MockMvcRequestBuilders::get,
                        "post", MockMvcRequestBuilders::post,
                        "put", MockMvcRequestBuilders::put,
                        "delete", MockMvcRequestBuilders::delete,
                        "patch", MockMvcRequestBuilders::patch);
        return methodMap.get(method)
                .apply(url)
                .params(convertToMultiValueMap(params))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    private static Stream<Arguments> getTestData() throws Exception {
        List<Arguments> args = new ArrayList<>();
        addArgsForGet(args);
        addArgsForGetAllByTaskId(args);
        addArgsForCreate(args);
        addArgsForUpdate(args);
        addArgsForDelete(args);

        return args.stream();
    }

    private static void addArgsForGet(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/api/v1/comment/1",
                        Map.of(), Strings.EMPTY, "user", USER_ROLES, 403),
                Arguments.of("get", "/api/v1/comment/1",
                        Map.of(), Strings.EMPTY, "admin", ADMIN_ROLES, 200),
                Arguments.of("get", "/api/v1/comment/1",
                        Map.of(), Strings.EMPTY, null, null, 403)));
    }

    private static void addArgsForGetAllByTaskId(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/api/v1/comment/task/1",
                        Map.of(), Strings.EMPTY, "user", USER_ROLES, 403),
                Arguments.of("get", "/api/v1/comment/task/1",
                        Map.of(), Strings.EMPTY, "admin", ADMIN_ROLES, 200),
                Arguments.of("get", "/api/v1/comment/task/1",
                        Map.of(), Strings.EMPTY, null, null, 403)));
    }

    private static void addArgsForCreate(List<Arguments> args) throws Exception {
        CommentDtoCreateRequest comment = CommentDtoCreateRequest.builder()
                .text("some text")
                .authorId(2L)
                .taskId(1L)
                .build();

        String content = mapper.writeValueAsString(comment);

        args.addAll(List.of(
                Arguments.of("post", "/api/v1/comment",
                        Map.of(), content, "user", USER_ROLES, 201),
                Arguments.of("post", "/api/v1/comment",
                        Map.of(), content, "admin", ADMIN_ROLES, 201),
                Arguments.of("post", "/api/v1/comment",
                        Map.of(), content, null, null, 403)));
    }

    private static void addArgsForUpdate(List<Arguments> args) {
        Map<String, String> params = Map.of("id", "1", "comment", "some comment");

        args.addAll(List.of(
                Arguments.of("put", "/api/v1/comment",
                        params, Strings.EMPTY, "user", USER_ROLES, 200),
                Arguments.of("put", "/api/v1/comment",
                        params, Strings.EMPTY, "admin", ADMIN_ROLES, 200),
                Arguments.of("put", "/api/v1/comment",
                        params, Strings.EMPTY, null, null, 403)));
    }

    private static void addArgsForDelete(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("delete", "/api/v1/comment/1",
                        Map.of(), Strings.EMPTY, "user", USER_ROLES, 403),
                Arguments.of("delete", "/api/v1/comment/1",
                        Map.of(), Strings.EMPTY, "admin", ADMIN_ROLES, 200),
                Arguments.of("delete", "/api/v1/comment/1",
                        Map.of(), Strings.EMPTY, null, null, 403)));
    }
}
