package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final UserService userService;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return TodoSaveResponse.from(savedTodo);
    }

    public Page<TodoResponse> getTodos(AuthUser authUser, int page, int size, String weather, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        // 날씨 조건 & 수정일 조건 모두 있을 시, 모든 조건으로 검색
        if (weather != null && (startDate != null && endDate != null)) {
            return todoRepository.findByWeatherAndDate(weather, startDateTime, endDateTime, pageable)
                    .map(TodoResponse::from);
        }

        // 날씨 조건 있을 시, 날씨 조건으로 검색
        if (weather != null) {
            return todoRepository.findByWeather(weather, pageable)
                    .map(TodoResponse::from);
        }

        // 수정일 조건 있을 시, 수정일 조건으로 검색
        if (startDate != null && endDate != null) {
            return todoRepository.findByStartDateAndEndDate(startDateTime, endDateTime, pageable)
                    .map(TodoResponse::from);
        }

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        return todos.map(TodoResponse::from);
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        return TodoResponse.from(todo);
    }

    public Page<TodoSearchResponse> searchTodos(
            String title,
            LocalDate startDate,
            LocalDate endDate,
            String nickName,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return todoRepository.searchTodos(title, startDate, endDate, nickName, pageable);
    }
}
