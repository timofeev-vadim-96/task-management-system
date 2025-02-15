package com.effectivemobile.taskmanagementsystem.mapper;

import java.util.Collection;
import java.util.List;

public interface DtoMapper<DTO, ENTITY> {
    DTO convertToDto(ENTITY comment);

    List<DTO> convertToDtos(Collection<ENTITY> comments);
}
