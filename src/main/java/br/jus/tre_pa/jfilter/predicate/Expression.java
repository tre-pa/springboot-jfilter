package br.jus.tre_pa.jfilter.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import br.jus.tre_pa.jfilter.jpa.AttributePath;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe base dos predicados de comparação e operadores lógicos (AND,OR).
 * 
 * @author jcruz
 *
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
// @formatter:off
@JsonSubTypes({
	@Type(value=Conjunction.class, name="and"),
	@Type(value=Disjunction.class, name="or"),
	@Type(value=EqualsPredicate.class, name="="),
	@Type(value=NotEqualsToPredicate.class, name="<>"),
	@Type(value=GreaterThanPredicate.class, name=">"),
	@Type(value=GreaterThanOrEqualsToPredicate.class, name=">="),
	@Type(value=LessThanPredicate.class, name="<"),
	@Type(value=LessThanOrEqualsToPredicate.class, name="<="),
	@Type(value=ContainsPredicate.class, name="contains"),
	@Type(value=NotContainsPredicate.class, name="notcontains")
})
// @formatter:on
public abstract class Expression {
	private String type;

	public abstract String toSql();

	public abstract String toJpql();

	public abstract <T> Predicate toPredicate(Class<T> clazz, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Map<String, AttributePath> paths);
}
