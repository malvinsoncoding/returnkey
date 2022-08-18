package com.rkey.returnkeyapp.service.mapper;

import com.rkey.returnkeyapp.domain.Returns;
import com.rkey.returnkeyapp.service.dto.ReturnsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Returns} and its DTO {@link ReturnsDTO}.
 */
@Mapper(componentModel = "spring", uses = { PendingMapper.class, OrderMapper.class })
public interface ReturnsMapper extends EntityMapper<ReturnsDTO, Returns> {
    @Mapping(target = "pending", source = "pending")
    @Mapping(target = "orders", source = "orders")
    ReturnsDTO toDto(Returns s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReturnsDTO toDtoId(Returns returns);
}
