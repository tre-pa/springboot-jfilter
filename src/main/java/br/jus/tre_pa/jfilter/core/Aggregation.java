package br.jus.tre_pa.jfilter.core;

import br.jus.tre_pa.jfilter.core.Aggregable.Operation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Aggregation {

	private String dataField;

	private Object result;

	private Operation operation;

	public Aggregation(String dataField, Object result, Operation operation) {
		super();
		this.dataField = dataField;
		this.result = result;
		this.operation = operation;
	}

}
