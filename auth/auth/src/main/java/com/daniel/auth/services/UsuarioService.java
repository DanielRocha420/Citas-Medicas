package com.daniel.auth.services;

import java.util.Set;

import com.daniel.auth.dto.UsuarioRequest;
import com.daniel.auth.dto.UsuarioResponse;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse eliminar(String username);
}

