package gaian.svsa.ct.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import gaian.svsa.ct.modelo.Atendimento;
import gaian.svsa.ct.modelo.enums.CodigoAuxiliarAtendimento;
import gaian.svsa.ct.service.AgendamentoIndividualService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author murakamiadmin
 *
 */
@Getter
@Setter
@Named
@ViewScoped
public class CadastroAtendimentoRecepcaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Atendimento item;
	private String nomePessoaAtendida;

	@Inject
	AgendamentoIndividualService atendimentoService;	
	@Inject
	LoginBean loginBean;
	
		
	@PostConstruct
	public void inicializar() {
		
		this.limpar();		
		
	}
	
	public void salvar() {
		try {			
			item.setCodigoAuxiliar(CodigoAuxiliarAtendimento.ATENDIMENTO_RECEPCAO);
			item.setRole(loginBean.getUsuario().getRole());
			item.setConselheiro(loginBean.getUsuario());
			item.setUnidade(loginBean.getUsuario().getUnidade());
			String str = " : ";
			String resumoNome = nomePessoaAtendida.concat(str).concat(item.getResumoAtendimento());
			item.setResumoAtendimento(resumoNome);
			
			this.atendimentoService.salvarResumoRecepcao(item);
			MessageUtil.sucesso("Atendimento Receção salvo com sucesso!");
			this.limpar();
			
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.erro("Erro desconhecido. Contatar o administrador");
		}
		
		this.limpar();
	}
	
	public void limpar() {
		this.item = new Atendimento();
		this.item.setTenant_id(loginBean.getTenantId());
	}

}