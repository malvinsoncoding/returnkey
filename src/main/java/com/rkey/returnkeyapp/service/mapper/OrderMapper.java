package com.rkey.returnkeyapp.service.mapper;

import com.rkey.returnkeyapp.domain.Order;
import com.rkey.returnkeyapp.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring", uses = { ReturnsMapper.class })
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "returns", source = "returns", qualifiedByName = "id")
    OrderDTO toDto(Order s);
}
