package br.jus.tjrj.indicadores_disponibilidade_pje.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@Entity
@Table(name = "tb_origem_indicador_disponibilidade_pje", schema = "tjrj")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Origem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String origem;

    @Column(nullable = false)
    private String descricao;

}