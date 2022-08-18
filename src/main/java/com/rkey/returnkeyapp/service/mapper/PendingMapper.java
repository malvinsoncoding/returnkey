package com.rkey.returnkeyapp.service.mapper;

import com.rkey.returnkeyapp.domain.Pending;
import com.rkey.returnkeyapp.service.dto.PendingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pending} and its DTO {@link PendingDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PendingMapper extends EntityMapper<PendingDTO, Pending> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PendingDTO toDtoId(Pending pending);
}
