package br.com.cotiinformatica.services;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cotiinformatica.components.JwtTokenComponent;
import br.com.cotiinformatica.components.RabbitMQProducerComponent;
import br.com.cotiinformatica.components.SHA256Component;
import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.CriarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.MensagemUsuarioDto;
import br.com.cotiinformatica.entities.Usuario;
import br.com.cotiinformatica.repositories.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private SHA256Component sha256Component; 
	
	@Autowired
	JwtTokenComponent jwtTokenComponent;
	
	@Autowired
	private RabbitMQProducerComponent rabbitMQProducerComponent;
	
	public CriarUsuarioResponseDto criar(CriarUsuarioRequestDto request) throws Exception {
		
		Usuario usuario = new Usuario();
		usuario.setId(UUID.randomUUID());
		usuario.setNome(request.getNome());
		usuario.setEmail(request.getEmail());
		usuario.setSenha(sha256Component.hash(request.getSenha()));
		
		if (usuarioRepository.findByEmail(usuario.getEmail()) != null)
			throw new IllegalArgumentException("O email informado já está cadastrado. Tente outro.");
		
		usuarioRepository.save(usuario);
		
		MensagemUsuarioDto mensagem = new MensagemUsuarioDto();
		mensagem.setEmailUsuario(usuario.getEmail());
		mensagem.setAssunto("Conta de usuário criado com sucesso - COTI Informática");
		mensagem.setTexto("Parabéns, " + usuario.getNome() + ", sua conta de usuário foi criada com sucesso.\n\nAtt\nEquipe COTI.");
		
		rabbitMQProducerComponent.sendMessage(mensagem);
		
		CriarUsuarioResponseDto response = new CriarUsuarioResponseDto();
		response.setDataHoraCadastro(new Date());
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		
		return response;
	}
	
	// Serviço para realizar a autenticação do usuário
	public AutenticarUsuarioResponseDto autenticar(AutenticarUsuarioRequestDto request) throws Exception {

		Usuario usuario = usuarioRepository.findByEmailAndSenha(request.getEmail(), sha256Component.hash(request.getSenha()));
		
		if (usuario == null)
			throw new IllegalAccessException("Acesso negado. Usuário não encontrado.");
		
		AutenticarUsuarioResponseDto response = new AutenticarUsuarioResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataHoraAcesso(new Date());
		response.setAccessToken(jwtTokenComponent.generateToken(usuario));
		
		return response;
	}
}
