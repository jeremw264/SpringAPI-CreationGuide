package com.example.demo.util;

import java.util.List;

public interface BaseMapper<ENTITY, DTO> {

    DTO toDto(ENTITY entity);

    List<DTO> toDtoList(List<ENTITY> entityList);

}