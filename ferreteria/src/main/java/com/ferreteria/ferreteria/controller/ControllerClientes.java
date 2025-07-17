package com.ferreteria.ferreteria.controller;

import com.ferreteria.ferreteria.model.Clientes;
import com.ferreteria.ferreteria.model.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")

public class ControllerClientes {
    @Autowired
    private ClientesRepository clientesRepository;


    //Devuelve todos los clientes
    @GetMapping
    public List<Clientes> getAll()
    {
        return clientesRepository.findAll();
    }

    @GetMapping("/{id_cliente}")
    public Clientes getById(Long id_cliente)
    {
        return clientesRepository.findById(id_cliente).orElse(null);
    }

    //Crear un nuevo cliente

    @PostMapping
    public Clientes create(@RequestBody Clientes cliente)
    {
        return clientesRepository.save(cliente);
    }

    //Actualizar un cliente
    @PutMapping("/{id}")
    public Clientes update(@PathVariable Long id,@RequestBody Clientes cliente)
    {
        cliente.setId_cliente(id);
        return clientesRepository.save(cliente);
    }

    //Eliminar un cliente
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id)
    {
        clientesRepository.deleteById(id);
    }

}
