package br.jus.tre_pa.jfilter.jpa;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import br.jus.tre_pa.jfilter.core.Aggregable;
import br.jus.tre_pa.jfilter.core.Aggregation;
import br.jus.tre_pa.jfilter.core.Page;
import br.jus.tre_pa.jfilter.core.Payload;
import br.jus.tre_pa.jfilter.core.Projectable;

/**
 * Interface de Repository com as funcionalidades de projeção, filtragem e agregação.
 * 
 * @author jcruz
 *
 * @param <T>
 */
public interface JFilterRepository<T> {

	Page<T> findAll(Class<T> entityClass, Pageable pageable, Projectable projectable, Class<? extends AbstractSpecification<T>> specificationClass, Payload payload);

	List<Aggregation> aggregation(Class<T> entityClass, Specification<T> specification, Aggregable... aggregables);
}
