package br.jus.tre_pa.jfilter.repository.specification;

import javax.persistence.criteria.JoinType;

import org.springframework.stereotype.Component;

import br.jus.tre_pa.jfilter.jpa.AbstractSpecification;
import br.jus.tre_pa.jfilter.repository.domain.Foo;

@Component
public class FooSpecification extends AbstractSpecification<Foo> {

	@Override
	protected void configure() {
		map("bar.name", String.class, root -> root.join("bar", JoinType.LEFT).get("name"));
	}

}
