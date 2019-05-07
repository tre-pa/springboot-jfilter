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
public class NotContainsPredicate extends AbstractPredicate {

	@Override
	public String toSql() {
		return this.getDataField() + " not like '%" + this.getValue() + "%'";
	}

	@Override
	public String toJpql() {
		return this.getDataField() + " not like '%" + this.getValue() + "%'";
	}

	@Override
	public <T> Predicate toPredicate(Class<T> clazz, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Map<String, AttributePath> paths) {
		AttributePath jiiAttr = paths.get(this.getDataField());
		Class<?> dataFieldType = Objects.nonNull(jiiAttr) ? jiiAttr.getFieldType() : getDataType(clazz, this.getDataField());
		Path<?> path = Objects.nonNull(jiiAttr) ? jiiAttr.getMap().apply(root) : root.get(this.getDataField());

		if (dataFieldType == String.class) return cb.notLike(cb.upper(path.as(String.class)), "%" + ((String) this.getValue()).toUpperCase() + "%");
		throw new IllegalArgumentException(String.format("Não foi possível montar um predicado para o campo '%s' com o tipo 'notcontains'", this.getValue()));
	}

}
