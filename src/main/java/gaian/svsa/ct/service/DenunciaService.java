package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import gaian.svsa.ct.modelo.Pessoa;

import gaian.svsa.ct.dao.DenunciaDAO;
import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.PessoaReferencia;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.util.NegocioException;
import gaian.svsa.ct.dao.RDComposicaoDAO;
import lombok.extern.log4j.Log4j;


/**
 * @author laurojr
 *
 */
@Log4j
public class DenunciaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DenunciaDAO denunciaDAO;
	
	@Inject
	private RDComposicaoDAO composicaoDAO;
	
	public void salvar(Denuncia denuncia) throws NegocioException {
		log.debug("Service : tenant = " + denuncia.getTenant_id());
		
		this.denunciaDAO.salvar(denuncia);
	}
	
	/*public void salvarAlterar(Denuncia denuncia, Long codigoUsuarioLogado) throws NegocioException {
		
		log.info("para verificar o problema de alteração de denuncia...criada por: " + denuncia.getConselheiro().getCodigo() + " **** Tentativa de alteração por : " + codigoUsuarioLogado);
			
		if(codigoUsuarioLogado.longValue() == denuncia.getConselheiro().getCodigo().longValue()) {
			if(new Date().after(DateUtils.plusDays(denuncia.getDataEmissao(), 7)) ){
				throw new NegocioException("Prazo para alteração (7 dias) foi ultrapassado!");
					
			}
			else {
				denunciaDAO.salvar(denuncia);
			}
		}
		else {
			throw new NegocioException("Somente o conselheiro que registrou pode alterar a denuncia! E isso só pode ser feito antes de 7 dias do registro.");
		}	
	} */
	
	
	public void transferirMembro(Pessoa pessoa, Long codigoProntDestino) throws NegocioException {
		
		// Recuperar prontuario destino	
		Denuncia denunciaDestino = denunciaDAO.buscarPeloCodigo(codigoProntDestino);
		
		pessoa.setFamilia(denunciaDestino.getFamilia());
		
		composicaoDAO.salvar(pessoa);
		
	}
	
	public void excluir(Denuncia denuncia) throws NegocioException {
		denunciaDAO.excluir(denuncia);
		
	}
	
	/* Ativa a denúncia */
	public void ativar(Denuncia denuncia) throws NegocioException {
		
		try {
			log.info("Ativando denúncia ... ");
			
			denunciaDAO.ativarDenuncia(denuncia);
			
		} catch (Exception e) {
			throw new NegocioException("Não foi possível ativar a denúncia.");
		}		
	}
	
	/* Inativa a denúncia */
	public void inativar(Denuncia denuncia) throws NegocioException {
		
		try {
			log.info("Inativando denúncia ... ");
			
			denunciaDAO.inativarDenuncia(denuncia);
			
		} catch (Exception e) {
			throw new NegocioException("Não foi possível inativar a denúncia.");
		}
		
	}
	
/* Buscas */
	
	
	public List<String> buscarNomes(String query, Long tenantId) {		
		return denunciaDAO.buscarNomes(query, tenantId);
	}

	public Denuncia buscarPeloCodigo(long codigo) {
		return denunciaDAO.buscarPeloCodigo(codigo);
	}
	
	// buscar pessoa pelo nome
	public PessoaReferencia buscarPeloNome(String nome) {
		return denunciaDAO.buscarPeloNome(nome);
	}

	public List<Denuncia> buscarTodos(Long tenantId, Unidade unidade) {
		return denunciaDAO.buscarTodos(tenantId, unidade);
	}
	
	public List<Denuncia> buscarTodosDia(Long tenantId, Unidade unidade) {
		return denunciaDAO.buscarTodosDia(tenantId, unidade);
	}
	
	public Denuncia buscarDenuncia(Long codigo, Unidade unidade, Long tenantId) {
		
		//log.info(codigo);
		//log.info(unidade);
		//log.info(tenantId);
		return denunciaDAO.buscarDenuncia(codigo, unidade, tenantId);
	}
}
