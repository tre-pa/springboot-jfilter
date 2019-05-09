package br.jus.tre_pa.jfilter.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.jus.tre_pa.jfilter.predicate.Expression;
import lombok.Getter;
import lombok.Setter;

public abstract class Filterable extends Expression {

	@Getter
	@Setter
	private List<Expression> predicates = new ArrayList<>();

	@Override
	public String toSql() {
		// @formatter:off
		return this.predicates
				.stream()
				.map(p -> p.toSql())
				.collect(Collectors.joining());
		// @formatter:on
	}

	@Override
	public String toJpql() {
		// @formatter:off
		return this.predicates
				.stream()
				.map(p -> p.toJpql())
				.collect(Collectors.joining());
		// @formatter:on
	}

}
