package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(todo)
                .where(todo.id.eq(todoId))
                .leftJoin(todo.user, user)
                .fetchJoin() // N+1 문제 해결
                .fetchOne());
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(String title, LocalDate startDate, LocalDate endDate, String nickName, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (title != null && !title.isEmpty()) {
            builder.and(todo.title.containsIgnoreCase(title));
        }

        if (startDate != null && endDate != null) {
            builder.and(todo.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)));
        }

        if (nickName != null && !nickName.isEmpty()) {
            builder.and(todo.user.nickName.containsIgnoreCase(nickName));
        }

        List<TodoSearchResponse> results = jpaQueryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        todo.title,
                        todo.managerCount,
                        todo.commentCount
                ))
                .from(todo)
                .where(builder)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(todo.count())
                .from(todo)
                .where(builder);

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

}
