package br.jus.tre_pa.jfilter.rest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonView;

import br.jus.tre_pa.jfilter.jpa.AbstractSpecification;
import br.jus.tre_pa.jfilter.jpa.JFilterRepository;

/**
 * 
 * @author jcruz
 *
 * @param <T>  Entidade JPA.
 * @param <ID> Tipo do atributo identificador.
 * @param <R>  Classe repository.
 */
public abstract class AbstractCrudRest<T, ID, S extends AbstractSpecification<T>, R extends JpaRepository<T, ID> & JFilterRepository<T>> extends AbstractFilterRest<T, ID, S, R> {

	/**
	 * Retorna a listagem paginada de recursos
	 * 
	 * @param pageable Informações de paginação.
	 * @return Objeto page.
	 */
	@GetMapping
	public final ResponseEntity<Page<T>> _findAll(Pageable pageable) {
		return this.findAll(pageable);
	}

	protected ResponseEntity<Page<T>> findAll(Pageable pageable) {
		return ResponseEntity.ok().body(getRepository().findAll(pageable));
	}

	/**
	 * Retorna um recurso específico.
	 * 
	 * @param id Identificador do recurso.
	 * @return Entidade gerenciada.
	 */
	@GetMapping(path = "/{id}")
	@JsonView(Views.Default.class)
	public final T _findById(@PathVariable ID id) {
		return this.findById(id);
	}

	protected T findById(ID id) {
		return getRepository().findById(id).orElseThrow(() -> new EntityNotFoundException("Entidade não encontrada."));
	}

	/**
	 * Retorna um recurso específico detalhado.
	 * 
	 * @param id Identificador do recurso.
	 * @return Entidade gerenciada.
	 */
	@GetMapping(path = "/{id}/detail")
	@JsonView(Views.Detail.class)
	public final T _findDetailById(@PathVariable ID id) {
		return this.findDetailById(id);
	}

	public T findDetailById(ID id) {
		return getRepository().findById(id).orElseThrow(() -> new EntityNotFoundException("Entidade não encontrada."));
	}

	/**
	 * Cria um novo recurso
	 *
	 * @param recurso
	 * @return Entidade gerenciada.
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public final T _insert(@RequestBody @Valid T entity) {
		return this.insert(entity);
	}

	protected T insert(T entity) {
		return getRepository().save(entity);
	}

	/**
	 * Atualiza um recurso
	 *
	 * @param recurso Recurso para atualização.
	 * @return Entidade gerenciada.
	 */
	@PutMapping(path = "/{id}")
	public final T _update(@PathVariable ID id, @RequestBody T entity) {
		return this.update(id, entity);
	}

	protected T update(ID id, T entity) {
		if (!getRepository().existsById(id)) throw new EntityNotFoundException("Entidade não encontrada.");
		return getRepository().save(entity);
	}

	/**
	 * Deleta um recurso
	 *
	 * @param id Identificador do recurso.
	 */
	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public final void _delete(@PathVariable ID id) {
		this.delete(id);
	}

	protected void delete(@PathVariable ID id) {
		if (!getRepository().existsById(id)) throw new EntityNotFoundException("Entidade não encontrada.");
		getRepository().deleteById(id);
	}
}
