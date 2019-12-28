package com.space.controller;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


    public class ShipSpecificationsBuilder {

        private final List<SearchCriteria> params;

        public ShipSpecificationsBuilder() {
            params = new ArrayList<SearchCriteria>();
        }

        public ShipSpecificationsBuilder with(String key, String operation, Object value) {
            params.add(new SearchCriteria(key, operation, value));
            return this;
        }

        public Specification<Ship> build() {
            if (params.size() == 0) {
                return null;
            }

            List<Specification> specs = params.stream()
                    .map(ShipSpecification::new)
                    .collect(Collectors.toList());

            Specification result = specs.get(0);

            for (int i = 1; i < params.size(); i++) {
                result = Specification.where(result).and(specs.get(i));

            }
            return result;
        }
    }

