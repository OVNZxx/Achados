package com.br.projetointegrador.pi.dto.dashboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DashboardResponse(
    @Valid @NotNull Long usuariosCadastrados,
    @Valid @NotNull Long publicacoesAtivas,
    @Valid @NotNull Long publicacoesFinalizadas,
    @Valid @NotNull Long perdidos,
    @Valid @NotNull Long encontrados
) {
    public DashboardResponse(Long usuariosCadastrados, Long publicacoesAtivas, Long publicacoesFinalizadas, Long perdidos,
    Long encontrados) {
        this.usuariosCadastrados = usuariosCadastrados;
        this.publicacoesAtivas = publicacoesAtivas;
        this.publicacoesFinalizadas = publicacoesFinalizadas;
        this.perdidos = perdidos;
        this.encontrados = encontrados;
    }
}
