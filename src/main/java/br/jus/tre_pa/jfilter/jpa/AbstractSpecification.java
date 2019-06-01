package br.jus.tre_pa.jfilter.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import br.jus.tre_pa.jfilter.core.Filterable;

/**
 * Classe abstrata de Specification.
 *
 */
public abstract class AbstractSpecification<T> {

	private Map<String, AttributePath> paths = new HashMap<>();

	@PostConstruct
	private void init() {
		this.configure();
	}

	/**
	 * Specification variável com os predicados definidos pelo usuário via
	 * Filterable.
	 * 
	 * @param entityClass Entidade alvo da Specification
	 * @param filterable  Classe Filterable com os predicados definidos pelo
	 *                    usuário.
	 * @return Specification
	 */
	public final Specification<T> variable(Class<T> entityClass, Filterable filterable) {
		return (root, cq, cb) -> filterable.toPredicate(entityClass, root, cq, cb, paths);
	}

	/**
	 * Specification fixas.
	 * 
	 * @return
	 */
	public Specification<T> fixed() {
		return (root, cq, cb) -> null;
	}

	/**
	 * Método que deve ser implementado com o mapeamento dos relacionamentos e os
	 * respetivos Path (Criateria API).
	 * 
	 * <p>
	 * <code>
	 * &#64;Override protected void configure() {
	 * 		 map("bar.name", String.class, root -> root.join("bar", JoinType.LEFT).get("name")); 
	 * }
	 * </code>
	 * </p>
	 */
	protected void configure() {}

	protected final void map(String path, Class<?> fieldType, Function<Root<?>, Path<?>> map) {
		paths.put(path, new AttributePath(fieldType, map));
	}

	public final Map<String, AttributePath> getPaths() {
		return paths;
	}

}
