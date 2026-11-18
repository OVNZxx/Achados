package com.br.projetointegrador.pi.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity 
@Getter 
@Setter 
public class Contato {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idContato;

    @ManyToOne 
    private Usuario idUsuario;

    @NotBlank 
    @Column (unique = true, length = 100)
    private String contato;
}
