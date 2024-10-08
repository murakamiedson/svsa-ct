package gaian.svsa.ct.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;

import gaian.svsa.ct.dao.lazy.LazyUsuario;
import gaian.svsa.ct.modelo.Usuario;
import gaian.svsa.ct.modelo.enums.Grupo;
import gaian.svsa.ct.service.UsuarioService;
import gaian.svsa.ct.util.MessageUtil;
import gaian.svsa.ct.util.NegocioException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author murakamiadmin
 *
 */
@Log4j
@Getter
@Setter
@Named
@ViewScoped
public class PesquisaUsuarioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Usuario> usuarios = new ArrayList<>();
	
	private LazyDataModel<Usuario> lazyUsuarios;
	
	private Usuario usuarioSelecionado;
	private Usuario usuarioSelecionadoExcluir;
	
	@Inject
	UsuarioService usuarioService;
	@Inject
	private LoginBean loginBean;
		
		
	@PostConstruct
	public void inicializar() {
		lazyUsuarios = new LazyUsuario( usuarioService, loginBean.getUsuario(), loginBean.getTenantId());
	}

	public void setUsuarioSelecionadoExcluir(Usuario usuarioSelecionadoExcluir) {
		this.usuarioSelecionadoExcluir = usuarioSelecionadoExcluir;
		log.info("usuarioSelecionadoExcluir setado = " + usuarioSelecionadoExcluir.getNome());
	}
	
	public void excluir() {
		try {
			usuarioService.excluir(usuarioSelecionadoExcluir);
			this.usuarios.remove(usuarioSelecionadoExcluir);
			MessageUtil.sucesso("Usuario " + usuarioSelecionadoExcluir.getNome() + " excluído com sucesso.");
		} catch (NegocioException e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public void reset() {
		try {
			usuarioService.reset(usuarioSelecionadoExcluir);	
			MessageUtil.sucesso("Senha do usuário " + usuarioSelecionadoExcluir.getNome() + " reinicializada com sucesso.");
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtil.erro(e.getMessage());
		}
	}
	
	public boolean isCoordenador() {
		if((loginBean.getUsuario().getGrupo() == Grupo.COORDENADORES) || 
				(loginBean.getUsuario().getGrupo() == Grupo.GESTORES))
			return true;
		
		return false;
	}

}