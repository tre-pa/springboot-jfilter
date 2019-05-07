package br.jus.tre_pa.jfilter.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import br.jus.tre_pa.jfilter.Filterable;
import lombok.Getter;

/**
 * Classe abstrata de Specification.
 *
 */
@Getter
public abstract class AbstractSpecification<T> {

	private Map<String, AttributePath> paths = new HashMap<>();

	@PostConstruct
	private void init() {
		this.configure();
	}

	/**
	 * Specification variável com os predicados definidos pelo usuário via Filterable.
	 * 
	 * @param entityClass Entidade alvo da Specification
	 * @param filterable  Classe Filterable com os predicados definidos pelo usuário.
	 * @return Specification
	 */
	@SuppressWarnings("serial")
	public Specification<T> variable(Class<T> entityClass, Filterable filterable) {
		return new Specification<T>() {

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				return filterable.toPredicate(entityClass, root, query, criteriaBuilder, paths);
			}
		};
	}

	/**
	 * Specification fixas.
	 * 
	 * @return
	 */
	@SuppressWarnings("serial")
	public Specification<T> fixed() {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				return null;
			}
		};
	}

	/**
	 * Método que deve ser implementado com o mapeamento dos relacionamentos e os respetivos Path (Criateria API).
	 * 
	 * <code>
	 * &#64;Override protected void configure() {
	 * 		 map("bar.name", String.class, root -> root.join("bar", JoinType.LEFT).get("name")); 
	 * }
	 * </code>
	 * 
	 */
	protected void configure() {}

	protected final void map(String path, Class<?> fieldType, Function<Root<?>, Path<?>> map) {
		paths.put(path, new AttributePath(fieldType, map));
	}
}
