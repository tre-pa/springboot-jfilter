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

/**
 * 
 * Classe que representa uma condição 'igual ou maior que' (>=).
 * 
 * @author jcruz
 *
 */
public class GreaterThanOrEqualsToPredicate extends AbstractPredicate {

	@Override
	public String toSql() {
		return this.getDataField() + " >= " + this.surroundSingleQuotes(this.getValue());
	}

	@Override
	public String toJpql() {
		return this.getDataField() + " >= " + this.surroundSingleQuotes(this.getValue());
	}

	@Override
	public <T> Predicate toPredicate(Class<T> clazz, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Map<String, AttributePath> paths) {
		AttributePath jiiAttr = paths.get(this.getDataField());
		Class<?> dataFieldType = Objects.nonNull(jiiAttr) ? jiiAttr.getFieldType() : getDataType(clazz, this.getDataField());
		Path<?> path = Objects.nonNull(jiiAttr) ? jiiAttr.getMap().apply(root) : root.get(this.getDataField());

		if (dataFieldType == LocalDate.class) return cb.greaterThanOrEqualTo(path.as(LocalDate.class), getLocalDateValue(this.getValue()));
		if (dataFieldType == LocalDateTime.class) return cb.greaterThanOrEqualTo(path.as(LocalDateTime.class), getLocalDateTimeValue(this.getValue()));
		if (dataFieldType == Long.class) return cb.ge(path.as(Number.class), (Number) this.getValue());
		if (dataFieldType == Integer.class) return cb.ge(path.as(Number.class), (Number) this.getValue());
		if (dataFieldType == Short.class) return cb.ge(path.as(Number.class), (Number) this.getValue());
		if (dataFieldType == BigDecimal.class) return cb.ge(path.as(Number.class), new BigDecimal(String.valueOf(this.getValue())));
		if (dataFieldType == BigInteger.class) return cb.ge(path.as(Number.class), new BigInteger(String.valueOf(this.getValue())));
		throw new IllegalArgumentException(String.format("Não foi possível montar um predicado para o campo '%s' com o tipo '>='", this.getDataField()));
	}

	private LocalDate  getLocalDateValue(Object value) {
		if (((String) this.getValue()).equals("@now")) return LocalDate.now();
		return LocalDate.parse((String) this.getValue(), DateTimeFormatter.ISO_DATE);
	}

	private LocalDateTime getLocalDateTimeValue(Object value) {
		if (((String) this.getValue()).equals("@now")) return LocalDateTime.now();
		return LocalDateTime.parse((String) this.getValue(), DateTimeFormatter.ISO_DATE_TIME);
	}

}
