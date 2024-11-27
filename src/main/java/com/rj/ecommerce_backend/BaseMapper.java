package com.rj.ecommerce_backend;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BaseMapper<D, E> {
    D toDto(E entity);
    E toEntity(D dto);

    List<D> toDtoList(List<E> entityList);
    List<E> toEntity(List<D> dtoList);
}
