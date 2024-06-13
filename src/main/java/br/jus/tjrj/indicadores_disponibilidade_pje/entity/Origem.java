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

    @Enumerated(EnumType.STRING) // Mapeia o enum para uma coluna do tipo String
    @Column(nullable = false, unique = true)
    private OrigemEnum origem; // Campo para armazenar o valor do enum

    @Column(nullable = false)
    private String descricao;

    // Enum dentro da entidade Origem
    public enum OrigemEnum {
        distribuicao,
        publicacao_do,
        grerj_vinculada
    }






}