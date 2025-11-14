package com.Clientes.services;

import com.Clientes.domain.Cliente;
import com.Clientes.repo.ClienteRepository;

import java.util.List;

import org.springframework.stereotype.Service;



@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    public Cliente actualizarCliente(Cliente clienteActualizado) {
        return clienteRepository.save(clienteActualizado);
    }
}
