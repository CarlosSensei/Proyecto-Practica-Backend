package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.loan.model.Loan;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;

public class LoanSpecification implements Specification<Loan> {

    public final SearchCriteria criteria;

    public LoanSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Loan> root, @Nullable CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(":") && criteria.getValue() != null) {

            Path<?> path = getPath(root);

            if (path.getJavaType() == String.class) {

                return builder.like(path.as(String.class), "%" + criteria.getValue() + "%");
            } else {

                return builder.equal(path, criteria.getValue());
            }
        }

        if (criteria.getOperation().equalsIgnoreCase("<=")) {

            return builder.lessThanOrEqualTo(getPath(root).as(LocalDate.class),(LocalDate) criteria.getValue());
        }

        if (criteria.getOperation().equalsIgnoreCase(">=")) {

            return builder.greaterThanOrEqualTo(getPath(root).as(LocalDate.class),(LocalDate) criteria.getValue());
        }

        return null;
    }

    private Path<?> getPath(Root<Loan> root) {

        String[] split = criteria.getKey().split("\\.");

        Path<?> expression = root.get(split[0]);

        for (int i = 1; i < split.length; i++) {
            expression = expression.get(split[i]);
        }

        return expression;
    }

}
