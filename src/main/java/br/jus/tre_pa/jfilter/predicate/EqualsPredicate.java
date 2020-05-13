package br.jus.tre_pa.jfilter.predicate;

import java.util.Map;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.jus.tre_pa.jfilter.jpa.AttributePath;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe que representa uma condição de igualdade (=).
 * 
 * @author jcruz
 *
 */
@Slf4j
public class EqualsPredicate extends AbstractPredicate {

	@Override
	public String toSql() {
		if (isBoolean()) return this.getDataField() + " = " + ((boolean) this.getValue() ? 1 : 0);
		if (getValue().equals("@null")) return String.format("%s is null ", this.getDataField());
		if (getValue().equals("@notnull")) return String.format("%s is not null ", this.getDataField());
		return this.getDataField() + " = " + this.surroundSingleQuotes(this.getValue());
	}

	@Override
	public String toJpql() {
		if (isBoolean()) return this.getDataField() + " is " + (boolean) this.getValue();
		if (getValue().equals("@null")) return "${this.dataField} is null ";
		if (getValue().equals("@notnull")) return "${this.dataField} is not null ";
		return this.getDataField() + " = " + this.surroundSingleQuotes(this.getValue());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> Predicate toPredicate(Class<T> clazz, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Map<String, AttributePath> paths) {
		AttributePath jiiAttr = paths.get(this.getDataField());
		Class<?> dataFieldType = Objects.nonNull(jiiAttr) ? jiiAttr.getFieldType() : getDataType(clazz, this.getDataField());
		Path<?> path = Objects.nonNull(jiiAttr) ? jiiAttr.getMap().apply(root) : root.get(this.getDataField());

		log.debug("EqualsPredicate [ field: {}, type: {}, value: {} ]", this.getDataField(), dataFieldType, this.getValue());

		if (dataFieldType == Boolean.class && (boolean) this.getValue() == true) return cb.isTrue(path.as(Boolean.class));
		if (dataFieldType == Boolean.class && (boolean) this.getValue() == false) return cb.isFalse(path.as(Boolean.class));
		if (this.getValue().equals("@notnull")) return cb.isNotNull(path);
		if (this.getValue().equals("@null")) return cb.isNull(path);
		if (dataFieldType.isEnum()) return cb.equal(path, Enum.valueOf((Class<Enum>) dataFieldType, String.valueOf(this.getValue())));
		return cb.equal(path, this.getValue());
	}
}
