package gaian.svsa.ct.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.enums.Status;
import gaian.svsa.ct.util.NegocioException;
import gaian.svsa.ct.util.jpa.Transactional;

/**
 * @author murakamiadmin
 *
 */
public class UsuarioDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	
	@Transactional
	public void salvar(Usuario usuario) throws PersistenceException, NegocioException {
		try {
			manager.merge(usuario);	
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw e;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Error e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		}
	}	
		
	@Transactional
	public void excluir(Usuario usuario) throws NegocioException {
			
		try {
			manager.merge(usuario);
		} catch (PersistenceException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		} catch (Error e) {
			e.printStackTrace();
			throw new NegocioException("Não foi possível executar a operação.");
		}
	}
	
	
	
	/*
	 * Buscas
	 */
	
	public Usuario buscarPeloCodigo(Long codigo) {
		return manager.find(Usuario.class, codigo);
	}
	
	public Usuario buscarPeloEmail(String email) throws NoResultException {
		return manager.createNamedQuery("Usuario.buscarPorEmail", Usuario.class)
				.setParameter("email", email)
				.getSingleResult();
	}	
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarTodos() {
		return manager.createNamedQuery("Usuario.buscarTodos").getResultList();
	}		
	
	
	/* Buscas caso de uso ManterSCFV, RealizarAtendimento ind e col e ManterPAIF */
	
	public List<Usuario> buscarConselheiros(Unidade unidade, Long tenantId) {
		return manager.createNamedQuery("Usuario.buscarConselheiros", Usuario.class)				
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", Status.ATIVO)
				.getResultList();
	}
	public List<Usuario> buscarConselheirosRole(Role role, Unidade unidade, Long tenantId) {
		return manager.createNamedQuery("Usuario.buscarConselheirosRole", Usuario.class)
				.setParameter("role", role)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", Status.ATIVO)
				.getResultList();
	}
	public List<Usuario> buscarUsuarios(Unidade unidade, Long tenantId) {
		return manager.createNamedQuery("Usuario.buscarUsuarios", Usuario.class)				
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", Status.ATIVO)
				.getResultList();
	}
	
	
	/* paginação */
	
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarComPaginacao(int first, int pageSize, Long tenantId) {
		return manager.createNamedQuery("Usuario.buscarTodos")
				.setParameter("tenantId", tenantId)
				.setFirstResult(first)
				.setMaxResults(pageSize)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarComPaginacao(int first, int pageSize, String nome, Long tenantId) {	
		return manager.createNamedQuery("Usuario.buscarTodosFiltro2")
			.setParameter("nome", "%" + nome.toUpperCase() + "%")
			.setParameter("tenantId", tenantId)
			.setFirstResult(first)
			.setMaxResults(pageSize)
			.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarComPaginacao(int first, int pageSize, Unidade unidade, Long tenantId) {
		
		return manager.createNamedQuery("Usuario.buscarTodosPorUnidade")
			.setParameter("unidade", unidade)
			.setParameter("tenantId", tenantId)
			.setFirstResult(first)
			.setMaxResults(pageSize)
			.getResultList();
	}	
	
	@SuppressWarnings("unchecked")
	public List<Usuario> buscarComPaginacao(int first, int pageSize, Unidade unidade, String nome, Long tenantId) {	
		
		return manager.createNamedQuery("Usuario.buscarTodosFiltro")
			.setParameter("unidade", unidade)
			.setParameter("tenantId", tenantId)
			.setParameter("nome", "%" + nome.toUpperCase() + "%")
			.setFirstResult(first)
			.setMaxResults(pageSize)
			.getResultList();
		}

	
	
	
	/* quantidades*/
	

	public Long encontrarQuantidadeDeUsuarios(Long tenantId) {
		return manager.createQuery("select count(u) from Usuario u where u.tenant.codigo = :tenantId", Long.class)
				.setParameter("tenantId", tenantId)
				.getSingleResult();
	}
	public Long encontrarQuantidadeDeUsuarios(String nome, Long tenantId) {
		return manager.createQuery("select count(u) from Usuario u where u.nome LIKE :nome "
				+ "and u.tenant.codigo = :tenantId", Long.class)
				.setParameter("tenantId", tenantId)
				.setParameter("nome", "%" + nome.toUpperCase() + "%")
				.getSingleResult();
	}
	public Long encontrarQuantidadeDeUsuarios(Unidade unidade, Long tenantId) {
		return manager.createQuery("select count(u) from Usuario u where u.unidade = :unidade "
				+ "and u.tenant.codigo = :tenantId", Long.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.getSingleResult();
	}
	public Long encontrarQuantidadeDeUsuarios(Unidade unidade, String nome, Long tenantId) {
		return manager.createQuery("select count(u) from Usuario u where u.unidade = :unidade "
				+ "and u.nome LIKE :nome "
				+ "and u.tenant.codigo = :tenantId", Long.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("nome", "%" + nome.toUpperCase() + "%")
				.getSingleResult();
	}
	public Long buscarTotalConselheiros(Long tenantId) {
		return manager.createNamedQuery("Usuario.buscarTotalConselheiros", Long.class)
				.setParameter("status", Status.ATIVO)
				.setParameter("tenantId", tenantId)
				.getSingleResult();
	}
	public Long buscarTotalConselheirosUnid(Unidade unidade, Long tenantId) {
		return manager.createNamedQuery("Usuario.buscarTotalConselheirosUnid", Long.class)
				.setParameter("unidade", unidade)
				.setParameter("tenantId", tenantId)
				.setParameter("status", Status.ATIVO)
				.getSingleResult();
	}
	
	
	
	
	
	// criado para realização de testes unitários com JIntegrity
	public void setEntityManager(EntityManager manager) {
		this.manager = manager;
	}

	public void teste() {
		System.out.println("teste");
	}

	
}