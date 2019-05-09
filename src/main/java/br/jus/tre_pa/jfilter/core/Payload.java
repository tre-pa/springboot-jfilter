package br.jus.tre_pa.jfilter.core;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payload {

	private Filterable filterable = new EmptyFilterable();

	private Aggregable[] aggregables;

	public static boolean hasAggregable(Payload payload) {
		return Objects.nonNull(payload) && Objects.nonNull(payload.getAggregables());
	}
}
