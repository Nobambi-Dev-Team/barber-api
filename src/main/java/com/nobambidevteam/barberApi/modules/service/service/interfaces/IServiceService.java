package com.nobambidevteam.barberApi.modules.service.service.interfaces;

import com.nobambidevteam.barberApi.modules.service.dto.ServiceCreateDto;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceDto;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IServiceService {

    List<ServiceDto> getAll();

    ServiceDto save(ServiceCreateDto request);

    ServiceDto update(UUID id, ServiceUpdateDto request);

    void deleteLogical(UUID id);
}
