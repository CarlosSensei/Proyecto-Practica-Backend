package com.ccsw.tutorial.game;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.game.model.Game;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.io.Serial;

public class GameSpecification implements Specification<Game> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final SearchCriteria criteria;

    public GameSpecification(SearchCriteria criteria) {

        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Game> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(":") && criteria.getValue() != null) {

            Path<String> path = getPath(root);

            if (path.getJavaType() == String.class) {
                return builder.like(path, "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(path, criteria.getValue());
            }
        }
        return builder.conjunction();
    }

    private Path<String> getPath(Root<Game> root) {

        String key = criteria.getKey();

        String[] split = key.split("[.]", 0);

        Path<String> expression = root.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            expression = expression.get(split[i]);
        }

        return expression;
    }

}
