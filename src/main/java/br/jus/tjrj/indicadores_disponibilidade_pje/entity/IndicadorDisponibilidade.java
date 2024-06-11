package br.jus.tjrj.indicadores_disponibilidade_pje.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tb_indicador_disponibilidade_pje", schema = "tjrj")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorDisponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private Integer diaDaSemana;

    @Column(nullable = false)
    private Integer hora;

    @Embedded
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "origem", referencedColumnName = "id", nullable = false)
    private Origem origem;

    @Column(nullable = false)
    private Integer quantidade;


}