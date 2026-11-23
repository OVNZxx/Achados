package com.br.projetointegrador.pi.model;



import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.br.projetointegrador.pi.enumerators.StatusPublicacao;
import com.br.projetointegrador.pi.enumerators.TipoPublicacao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
public class Publicacao {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idPublicacao;

    @ManyToOne 
    private Usuario idUsuario;

    @ManyToOne 
    private Local idLocal;

    @NotBlank (message = "O campo nome não pode ser estar vazio")
    private String nome;

    @CreationTimestamp 
    private LocalDateTime dataCriacao;

    @NotNull (message = "O campo data de ocorrência não pode ser nulo")
    private Date dataOcorrencia;

    @NotBlank (message = "O campo descrição não pode ser estar vazio")
    private String descricao;

    @Enumerated (EnumType.STRING)
    private StatusPublicacao status;

    @Enumerated (EnumType.STRING)
    private TipoPublicacao tipo;

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicacaoContato> contatosDisponibilizados = new ArrayList<>();
}
