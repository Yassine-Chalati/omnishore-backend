package com.omnishore.cvtech.domain.specifications;

import com.omnishore.cvtech.domain.entities.CvFile;
import com.omnishore.cvtech.dtos.filters.CvFileFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CvFileSpecifications {

    public static Specification<CvFile> withFilters(CvFileFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getFileName() != null && !filter.getFileName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("fileName")), "%" + filter.getFileName().toLowerCase() + "%"));
            }

            if (filter.getFileType() != null && !filter.getFileType().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("fileType")), filter.getFileType().toLowerCase()));
            }

            if (filter.getAddedDate() != null) {
                predicates.add(cb.equal(root.get("addedDate"), filter.getAddedDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
