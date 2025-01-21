package com.effectivemobile.taskmanagementsystem.dao;


import com.effectivemobile.taskmanagementsystem.model.Task;
import com.effectivemobile.taskmanagementsystem.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchCriteriaWithPaginationTaskDaoImpl implements SearchCriteriaWithPaginationTaskDao {
    @PersistenceContext
    private EntityManager entityManager;

    public Page<Task> findAll(List<SearchCriteria> params, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = builder.createQuery(Task.class);
        Root root = query.from(Task.class);

        Predicate predicate = builder.conjunction();

        SearchQueryCriteriaConsumer searchConsumer =
                new SearchQueryCriteriaConsumer(predicate, builder, root);
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();

        query.where(predicate);

        TypedQuery<Task> typedQuery = entityManager.createQuery(query);

        // Добавление пагинации
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Task> result = typedQuery.getResultList();
        long total = countTotal(params);

        return new PageImpl<>(result, pageable, total);
    }

    /**
     * Метод для подсчета общего количества записей в запросе
     *
     * @param params критерии поиска
     * @return общее количество записей
     */
    private long countTotal(List<SearchCriteria> params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root r = countQuery.from(Task.class);

        Predicate predicate = builder.conjunction();

        SearchQueryCriteriaConsumer searchConsumer =
                new SearchQueryCriteriaConsumer(predicate, builder, r);
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        countQuery.where(predicate);
        countQuery.select(builder.count(r));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
