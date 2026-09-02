package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;
import org.example.dto.TokenDTO;
import org.example.exception.RecursoNaoEncontrado;
import org.example.model.Usuario;
import org.example.repository.UsuarioRepository;
import org.example.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints públicos para registro e login de usuários")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Registra um novo usuário", description = "Cria um novo usuário com senha criptografada e já retorna um token JWT logado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso, token retornado")
    })
    @PostMapping("/register")
    public ResponseEntity<TokenDTO> register(@RequestBody RegisterDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha())); // nunca salva senha crua!

        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenDTO(token));
    }

    @Operation(summary = "Autentica um usuário", description = "Verifica email e senha e retorna um token JWT em caso de sucesso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso, token retornado"),
            @ApiResponse(responseCode = "404", description = "Email ou senha inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RecursoNaoEncontrado("Email ou senha inválidos"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RecursoNaoEncontrado("Email ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario);
        return ResponseEntity.ok(new TokenDTO(token));
    }
}
