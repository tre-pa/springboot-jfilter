package br.jus.tre_pa.jfilter.jpa;

import java.util.function.Function;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe com o mapeamento do tipo de atributo com o path jpa. Utilizada com a classe de Specification.
 * 
 * @author jcruz
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class AttributePath {
	/**
	 * Tipo do atributo
	 */
	private Class<?> fieldType;

	/**
	 * Path JPA.
	 */
	private Function<Root<?>, Path<?>> map;

	public AttributePath(Class<?> fieldType, Function<Root<?>, Path<?>> map) {
		super();
		this.fieldType = fieldType;
		this.map = map;
	}

}
