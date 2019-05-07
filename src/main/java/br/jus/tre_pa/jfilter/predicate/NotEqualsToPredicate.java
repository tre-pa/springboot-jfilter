package br.jus.tre_pa.jfilter.predicate;

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
 * @author jcruz
 *
 */
public class NotEqualsToPredicate extends AbstractPredicate {

	@Override
	public String toSql() {
		if (isBoolean()) return this.getDataField() + " = " + ((boolean) this.getValue() ? 0 : 1);
		if (getValue().equals("@null")) return this.getDataField() + " is not null ";
		if (getValue().equals("@notnull")) return this.getDataField() + " is null ";
		return this.getDataField() + " <> " + this.surroundSingleQuotes(this.getValue());
	}

	@Override
	public String toJpql() {
		if (isBoolean()) return this.getDataField() + " is " + !(boolean) this.getValue();
		if (getValue().equals("@null")) return this.getDataField() + " is not null ";
		if (getValue().equals("@notnull")) return this.getDataField() + " is null ";
		return this.getDataField() + " <> " + this.surroundSingleQuotes(this.getValue());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> Predicate toPredicate(Class<T> clazz, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Map<String, AttributePath> paths) {
		AttributePath jiiAttr = paths.get(this.getDataField());
		Class<?> dataFieldType = Objects.nonNull(jiiAttr) ? jiiAttr.getFieldType() : getDataType(clazz, this.getDataField());
		Path<?> path = Objects.nonNull(jiiAttr) ? jiiAttr.getMap().apply(root) : root.get(this.getDataField());

		if (dataFieldType == Boolean.class && (boolean) this.getValue() == true) return cb.isFalse(path.as(Boolean.class));
		if (dataFieldType == Boolean.class && (boolean) this.getValue() == false) return cb.isTrue(path.as(Boolean.class));
		if (getValue() == "@notnull") return cb.isNull(path);
		if (getValue() == "@null") return cb.isNotNull(path);
		if (dataFieldType.isEnum()) return cb.notEqual(path, Enum.valueOf((Class<Enum>) dataFieldType, String.valueOf(this.getValue())));
		return cb.notEqual(path, this.getValue());
	}

}
