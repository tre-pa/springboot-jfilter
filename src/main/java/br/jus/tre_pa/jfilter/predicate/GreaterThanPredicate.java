package br.jus.tre_pa.jfilter.predicate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.jus.tre_pa.jfilter.jpa.AttributePath;

public class GreaterThanPredicate extends AbstractPredicate {

	@Override
	public String toSql() {
		return this.getDataField() + " > " + this.surroundSingleQuotes(this.getValue());
	}

	@Override
	public String toJpql() {
		return this.getDataField() + " > " + this.surroundSingleQuotes(this.getValue());
	}

	@Override
	public <T> Predicate toPredicate(Class<T> clazz, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Map<String, AttributePath> paths) {
		AttributePath jiiAttr = paths.get(this.getDataField());
		Class<?> dataFieldType = Objects.nonNull(jiiAttr) ? jiiAttr.getFieldType() : getDataType(clazz, this.getDataField());
		Path<?> path = Objects.nonNull(jiiAttr) ? jiiAttr.getMap().apply(root) : root.get(this.getDataField());

		if (dataFieldType == LocalDate.class) return cb.greaterThan(path.as(LocalDate.class), LocalDate.parse((String) this.getValue(), DateTimeFormatter.ISO_DATE));
		if (dataFieldType == LocalDateTime.class) return cb.greaterThan(path.as(LocalDateTime.class), LocalDateTime.parse((String) this.getValue(), DateTimeFormatter.ISO_DATE_TIME));
		if (dataFieldType == Long.class) return cb.gt(path.as(Number.class), (Number) this.getValue());
		if (dataFieldType == Integer.class) return cb.gt(path.as(Number.class), (Number) this.getValue());
		if (dataFieldType == Short.class) return cb.gt(path.as(Number.class), (Number) this.getValue());
		if (dataFieldType == BigDecimal.class) return cb.gt(path.as(Number.class), new BigDecimal(String.valueOf(this.getValue())));
		if (dataFieldType == BigInteger.class) return cb.gt(path.as(Number.class), new BigInteger(String.valueOf(this.getValue())));
		throw new IllegalArgumentException(String.format("Não foi possível montar um predicado para o campo '%s' com o tipo '>'", this.getDataField()));
	}

}
