package br.jus.tre_pa.jfilter.jpa.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.jus.tre_pa.jfilter.core.Aggregable;
import br.jus.tre_pa.jfilter.core.Aggregation;
import br.jus.tre_pa.jfilter.core.Page;
import br.jus.tre_pa.jfilter.core.Payload;
import br.jus.tre_pa.jfilter.core.Projectable;
import br.jus.tre_pa.jfilter.core.TriFunction;
import br.jus.tre_pa.jfilter.core.Aggregable.Operation;
import br.jus.tre_pa.jfilter.jpa.AbstractSpecification;
import br.jus.tre_pa.jfilter.jpa.AttributePath;
import br.jus.tre_pa.jfilter.jpa.JFilterRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Repository
@Transactional(readOnly = true)
@Slf4j
public class JFilterRepositoryImpl<T> implements JFilterRepository<T> {

	@PersistenceContext
	private EntityManager entityManager;

	private Map<Operation, TriFunction<String, Root<T>, CriteriaBuilder, Expression<? extends Number>>> operations = new HashMap<>();

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	protected void init() {
		operations.put(Operation.COUNT, (dataField, root, cb) -> cb.count(root.get(dataField)));
		operations.put(Operation.SUM, (dataField, root, cb) -> cb.sum(root.get(dataField)));
		operations.put(Operation.MIN, (dataField, root, cb) -> cb.min(root.get(dataField)));
		operations.put(Operation.MAX, (dataField, root, cb) -> cb.max(root.get(dataField)));
		operations.put(Operation.AVG, (dataField, root, cb) -> cb.avg(root.get(dataField)));
	}

	@Override
	@SneakyThrows
	public Page<T> findAll(Class<T> entityClass, Pageable pageable, Projectable projectable, Class<? extends AbstractSpecification<T>> specificationClass, Payload payload) {
		if (Projectable.hasProjection(projectable)) {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<T> root = cq.from(entityClass);
			cq.select(cb.array(projectable.getFields().stream().map(field -> root.get(field)).toArray(Selection[]::new)));
			Specification<T> spec = getSpecification(entityClass, specificationClass, payload);
			this.prepareWhereDecl(root, cq, cb, spec);
			this.prepareOrderByDecl(root, cq, cb, pageable, specificationClass);
			TypedQuery<Object[]> q = this.createQuery(cq, pageable);
			// TODO Implementar mecanismo para retornar o atributo Id.
			Aggregation countAggregation = this.aggregationTemplate(entityClass, "id", spec, Operation.COUNT);

			List<T> results = objectArrayToEntityList(entityClass, projectable, q);

//			log.info("Results: {}", results);

			org.springframework.data.domain.Page<T> page = new PageImpl<T>(results, pageable, Long.class.cast(countAggregation.getResult()));
			if (Payload.hasAggregable(payload)) return new Page<T>(page, aggregation(entityClass, spec, payload.getAggregables()));
			return new Page<T>(page);
		}
		return this.findAll(entityClass, pageable, specificationClass, payload);
	}

	// FIXME Preparar para projeções de apenas 1 campo.
	@SneakyThrows
	private List<T> objectArrayToEntityList(Class<T> entityClass, Projectable projectable, TypedQuery<Object[]> q) {
		List<T> results = new ArrayList<>();
		for (Object[] result : q.getResultList()) {
			T entity = entityClass.getDeclaredConstructor().newInstance();
			for (int i = 0; i < projectable.getFields().size(); i++) {
				PropertyUtils.setSimpleProperty(entity, projectable.getFields().get(i), result[i]);
			}
			results.add(entity);
		}
		return results;
	}

	/**
	 * FindAll sem projeção.
	 * 
	 * @param entityClass
	 * @param pageable
	 * @param specificationClass
	 * @param payload
	 * @return
	 */
	private Page<T> findAll(Class<T> entityClass, Pageable pageable, Class<? extends AbstractSpecification<T>> specificationClass, Payload payload) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		Root<T> root = cq.from(entityClass);
		Specification<T> spec = getSpecification(entityClass, specificationClass, payload);
		this.prepareWhereDecl(root, cq, cb, spec);
		this.prepareOrderByDecl(root, cq, cb, pageable, specificationClass);
		TypedQuery<T> q = this.createQuery(cq, pageable);
		Aggregation countAggregation = this.aggregationTemplate(entityClass, "id", spec, Operation.COUNT);

		org.springframework.data.domain.Page<T> page = new PageImpl<T>(q.getResultList(), pageable, Long.class.cast(countAggregation.getResult()));
		if (Payload.hasAggregable(payload)) return new Page<T>(page, aggregation(entityClass, spec, payload.getAggregables()));
		return new Page<T>(page);
	}

	@Override
	public List<Aggregation> aggregation(Class<T> entityClass, Specification<T> specification, Aggregable... aggregables) {
		// @formatter:off
		return Arrays.asList(aggregables)
				.stream()
				.map (aggregable -> aggregationTemplate(entityClass, aggregable.getDataField(), specification, aggregable.getOperation()) )
				.collect(Collectors.toList());
		// @formatter:on
	}

	/*
	 * Retorna a Specification associadas a entidade T. O retorna é a união entre os predicados da specification fixa (fixedSpecification) juntamente com os predicados das
	 * specification variável (variableSpecification).
	 */
	private Specification<T> getSpecification(Class<T> entityClass, Class<? extends AbstractSpecification<T>> specificationClass, Payload payload) {
		if (Objects.nonNull(payload) && Objects.nonNull(payload.getFilterable())) {
			AbstractSpecification<T> specification = applicationContext.getBean(specificationClass);
			return specification.fixed().and(specification.variable(entityClass, payload.getFilterable()));
		}
		return null;
	}

	/*
	 * Prepara a cláusula Where da consulta.
	 */
	private <E> void prepareWhereDecl(Root<T> root, CriteriaQuery<E> cq, CriteriaBuilder cb, Specification<T> spec) {
		if (Objects.nonNull(spec) && Objects.nonNull(spec.toPredicate(root, cq, cb))) cq.where(spec.toPredicate(root, cq, cb));
	}

	/*
	 * Prepara a cláusula Order By da consulta.
	 */
	private <E> void prepareOrderByDecl(Root<T> root, CriteriaQuery<E> cq, CriteriaBuilder cb, Pageable pageable, Class<? extends AbstractSpecification<T>> specificationClass) {
		if (Objects.nonNull(specificationClass)) {
			AbstractSpecification<T> specification = applicationContext.getBean(specificationClass);
			if (Objects.nonNull(pageable) && Objects.nonNull(pageable.getSort())) {
				// @formatter:off
				cq.orderBy(pageable.getSort()
						.stream()
						.map(o -> {
							AttributePath attr = specification.getPaths().get(o.getProperty());
							Path<?> path = Objects.nonNull(attr) ?attr.getMap().apply(root): root.get(o.getProperty());
							return o.isAscending() ? cb.asc(path) : cb.desc(path);
						})
						.toArray(Order[]::new));
				// @formatter:on
			}
		}
	}

	/*
	 * 
	 */
	private <E> TypedQuery<E> createQuery(CriteriaQuery<E> cq, Pageable pageable) {
		TypedQuery<E> q = entityManager.createQuery(cq);
		if (Objects.nonNull(pageable)) {
			q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			q.setMaxResults(pageable.getPageSize());
		}
		return q;
	}

	/*
	 * Template para execução de operação de agregação
	 * 
	 */
	private Aggregation aggregationTemplate(Class<T> entityClass, String dataField, Specification<T> spec, Aggregable.Operation operation) {
		log.debug("[aggregationTemplate] => class: {}, dataField: {}, operation: {}", entityClass, dataField, operation);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Number> cq = cb.createQuery(Number.class);
		Root<T> root = cq.from(entityClass);
		cq.select(operations.get(operation).apply(dataField, root, cb));
		if (Objects.nonNull(spec) && Objects.nonNull(spec.toPredicate(root, cq, cb))) cq.where(spec.toPredicate(root, cq, cb));
		TypedQuery<Number> query = entityManager.createQuery(cq);
		return new Aggregation(dataField, query.getSingleResult(), operation);
	}
}
