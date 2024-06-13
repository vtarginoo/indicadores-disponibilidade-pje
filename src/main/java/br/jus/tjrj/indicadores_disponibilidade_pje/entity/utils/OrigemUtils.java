package br.jus.tjrj.indicadores_disponibilidade_pje.entity.utils;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.repository.OrigemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrigemUtils {

    @Autowired
    private static  OrigemRepository repository;


    private static Map<Origem.OrigemEnum, Long> getOrigemEnumToIdMap() {
        List<Origem> origens = repository.findAll();
        return origens.stream()
                .collect(Collectors.toMap(Origem::getOrigem, Origem::getId));
    }


    public static Long getOrigemIdByOrigemEnum(Origem.OrigemEnum origemEnum) {
        Map<Origem.OrigemEnum, Long> map = getOrigemEnumToIdMap();
        return map.get(origemEnum);
    }

    public static   Origem.OrigemEnum getOrigemEnumByOrigemId(Long origemId) {
        Map<Origem.OrigemEnum, Long> map = getOrigemEnumToIdMap();
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().equals(origemId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }






}
