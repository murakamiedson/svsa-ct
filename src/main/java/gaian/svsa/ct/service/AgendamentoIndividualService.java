package gaian.svsa.ct.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import gaian.svsa.ct.controller.LoginBean;
import gaian.svsa.ct.dao.AgendamentoIndividualDAO;
import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.modelo.Unidade;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.Role;
import gaian.svsa.ct.modelo.enums.StatusAtendimento;
import gaian.svsa.ct.modelo.to.AtendimentoDTO;
import gaian.svsa.ct.modelo.to.AtendimentoTO;
import gaian.svsa.ct.util.DateUtils;
import gaian.svsa.ct.util.NegocioException;

/**
 * @author murakamiadmin
 *
 */
public class AgendamentoIndividualService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(AgendamentoIndividualService.class);
	
	@Inject
	private AgendamentoIndividualHelper helper;	
	@Inject
	private AgendamentoIndividualDAO listaDAO;
	@Inject 
	private CalendarioAgendaHelper calendarioHelper;
	@Inject 
	private LoginBean loginBean;
	

	public void salvar(Atendimento lista, Long tenantId) throws NegocioException {
		
		log.info("Agendamento pessoa:  " + lista.getPessoa().getNome());
		
		verificarDisponibilidade(loginBean.getUsuario().getUnidade(), lista, tenantId);
		
		if(lista.getPessoa().getTelefone() == null 
				|| lista.getPessoa().getTelefone().isEmpty() 
				|| lista.getPessoa().getTelefone().contentEquals("")) {
			throw new NegocioException("Pessoa sem telefone de contato! Cadastre o telefone antes de agendar.");
		}
		
		lista.setStatusAtendimento(StatusAtendimento.AGENDADO);		
		
		this.listaDAO.salvar(lista);
	}

	public void verificarDisponibilidade(Unidade unidade, Atendimento item, Long tenantId) throws NegocioException {		
				
		if(item.getConselheiro() == null) {
			calendarioHelper.verificarDisponibilidade(unidade, item.getDataAgendamento(), tenantId);
		}
		else {
			calendarioHelper.verificarDisponibilidade(item.getConselheiro(), item.getDataAgendamento(), tenantId);
		}
		
	}
		
	

	/* RelatorioAtendimento migração */
	public void salvarMigrar(Atendimento lista) throws NegocioException {

		this.listaDAO.salvarAlterar(lista);
	}	
	
	public void atualizar(Atendimento lista) throws NegocioException {
		
		lista.setDataAtendimento(new Date());
		lista.setStatusAtendimento(StatusAtendimento.FALTOU);
		
		this.listaDAO.salvar(lista);
	}
	
	public void encerrarAtendimento(Atendimento lista) throws NegocioException {		
			
		try {
			lista.setStatusAtendimento(StatusAtendimento.ATENDIDO);		
			
			this.listaDAO.salvarEncerramento(lista);
			
				
			}catch(Exception ex) {
				ex.printStackTrace();
				throw new NegocioException("Problema na gravação do atendimento!");
			}
	}
	
	
	
	public void salvarResumoRecepcao(Atendimento lista) throws NegocioException {		
		
		if (lista.getDataAtendimento() == null ) {
			lista.setDataAtendimento(new Date());	
			log.info("Data atendimento = " + lista.getDataAtendimento());
		}		
		
		lista.setStatusAtendimento(StatusAtendimento.ATENDIDO);
		
		this.listaDAO.salvarRecepcao(lista);
	}
	
	public void autoSave(Atendimento lista) throws NegocioException {			
			
		if (lista.getDataAtendimento() == null ) {
			lista.setDataAtendimento(new Date());	
			log.info("Auto save = " + lista.getDataAtendimento() + " pessoa: " + lista.getPessoa().getNome());
		}
		
		this.listaDAO.salvar(lista);
	}
	
	public void salvarAlterar(Atendimento item, Usuario usuario) throws NegocioException {
		
		if(usuario.getCodigo().longValue() == item.getConselheiro().getCodigo().longValue()) {
			if(new Date().after(DateUtils.plusDays(item.getDataAtendimento(), 7)) )
			{
				throw new NegocioException("Prazo para alteração (7 dias) foi ultrapassado!");
				
			}
			else {
				listaDAO.salvarAlterar(item);
			}
		}
		else {
			throw new NegocioException("Somente o conselheiro que atendeu pode alterar o atendimento! E isso só pode ser feito antes de 7 dias do registro.");
		}
		
	}
	
	public void excluir(Atendimento item) throws NegocioException {
		listaDAO.excluir(item);
		
	}	
	
	
	// ----------------------------------------------------------------------------
	// Validações
	//
	
	/*
	public void verificarAgendamento(Prontuario prontuario, Long tenantId) throws NegocioException {
		if(listaDAO.buscarPorPessoa(prontuario.getFamilia().getPessoaReferencia(), tenantId) != 0)
			throw new NegocioException("Não é possível concluir a operação, pois há agendamento/atendimento pendente!");
	}
	
	*/
	
	
	
	
	
	public List<Atendimento> consultaFaltas(Pessoa pessoa, Long tenantId) {
		return helper.consultaFaltas(pessoa, tenantId);
	}
	
		
	public List<Atendimento> buscarAtendimentosRole(Usuario usuario, Long tenantId) {
		
		log.info(usuario.getRole().name());
		
		if(usuario.getRole() == Role.ADVOGADO ||
				usuario.getRole() == Role.CONSELHEIRO ||
				usuario.getRole() == Role.PSICOLOGO ||
				usuario.getRole() == Role.ORIENTADOR_SOCIAL) {
			return listaDAO.buscarAtendimentosConselheiros(usuario.getUnidade(), tenantId);
		}
		return listaDAO.buscarAtendimentosRole(usuario, tenantId);
	}
	
	public List<Atendimento> buscarAtendimentosRecepcao(Usuario usuario, Long tenantId) {
		
		/*
		 * Retorna apenas atendimentos dos ultimos 30 dias
		 */
		Calendar ini = Calendar.getInstance(); 
		ini.add(Calendar.DAY_OF_MONTH, -30);
		Calendar fim = Calendar.getInstance();
		
		log.info("Datas ini: " + ini.getTime() + "--Data fim: " + fim.getTime());
		
		return listaDAO.buscarAtendimentosRecepcao(usuario, ini.getTime(), fim.getTime(), tenantId);
	}

	/* Busca o histórico da pessoa */
	public List<AtendimentoDTO> buscarResumoAtendimentosDTO(Pessoa pessoa, Long tenantId) {	
		
		/* atendimentos individualizados (os coletivos são gravados aqui tambem)*/
		return helper.buscarResumoAtendimentosDTO(pessoa, tenantId);		
	}

	public List<Atendimento> buscarAtendimentosAgendados(Unidade unidade, Long tenantId) {
		return listaDAO.buscarAtendimentosAgendados(unidade, tenantId);
	}
	// usado pelo Agendamento e Atendimento Individualizado
	public List<Atendimento> buscarAtendimentosAgendados(Unidade unidade, Date mesAno, Long tenantId) {
		return listaDAO.buscarAtendimentosAgendados(unidade, mesAno, tenantId);
	}
	
	public List<Atendimento> buscarAtendimentosCodAuxGrafico(Unidade unidade, Date ini, Date fim, Long tenantId) {
		if(ini != null)
			if(fim != null)
				return listaDAO.buscarAtendimentosCodAuxGrafico(unidade, ini, fim, tenantId);
			else
				return listaDAO.buscarAtendimentosCodAuxGrafico(unidade, ini, new Date(), tenantId);
		return listaDAO.buscarAtendimentosCodAuxGrafico(unidade, tenantId);
	}
	
	
	/*
	 * RelatorioAtendimentos CadUnico 
	 */
	
	public List<Atendimento> buscarAtendCadUnicoPeriodo(Unidade unidade, Date ini, Date fim, Long tenantId) {
		return helper.buscarAtendCadUnicoPeriodo(unidade, ini, fim, tenantId);
	}
	public List<Atendimento> buscarAtendCadUnicoPeriodo2(Unidade unidade, Date ini, Date fim, Long tenantId) {
		return helper.buscarAtendCadUnicoPeriodo2(unidade, ini, fim, tenantId);
	}
	
	
	/*
	 * RelatorioAtendimentoFamilia  
	 */
	
	/*
	public List<ListaAtendimento> relatorioAtendimentoFamilia(Unidade unidade, Prontuario prontuario, Long tenantId) {
		return listaDAO.buscarAtendimentoFamilia(unidade, prontuario, tenantId);
	}
	
	*/
	
	/*
	 * RelatorioAtendimentosG  
	 */
	
	
	
	public List<AtendimentoTO> relatorioAtendimentosTO(Long tenantId) {			
		
		return helper.relatorioAtendimentosTO(tenantId);
	}
	
	public List<AtendimentoTO> relatorioAtendimentosTOCodAux(Unidade unidade, Date ini, Date fim, Long tenantId) {			
		
		return helper.relatorioAtendimentosTOCodAux(unidade, ini, fim, tenantId);
	}
	public List<AtendimentoTO> relatorioAtendimentosTOCodAux (Long tenantId) {			
		
		return helper.relatorioAtendimentosTOCodAux(tenantId);
	}

	
	public AgendamentoIndividualDAO getListaDAO() {
		return listaDAO;
	}
	
}
