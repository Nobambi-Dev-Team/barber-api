package com.nobambidevteam.barberApi.modules.service.service;

import com.nobambidevteam.barberApi.exceptions.BusinessRuleException;
import com.nobambidevteam.barberApi.exceptions.ResourceNotFoundException;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceCreateDto;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceDto;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceUpdateDto;
import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;
import com.nobambidevteam.barberApi.modules.service.mapper.ServiceMapper;
import com.nobambidevteam.barberApi.modules.service.repository.IServiceRepository;
import com.nobambidevteam.barberApi.modules.service.service.interfaces.IServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceService implements IServiceService {

    private final IServiceRepository serviceRepository;

    //alta
    @Override
    public ServiceDto save(ServiceCreateDto request) {

        // validaciones de negocio
        validateExistsService(request.name());

        // mapeo dto a entidad
        ServiceEntity serviceToSave = ServiceMapper.toEntity(request);

        // persistencia
        serviceToSave = serviceRepository.save(serviceToSave);

        // mapeo entidad a dto
        return ServiceMapper.toDto(serviceToSave);
    }

    private void validateExistsService(String serviceName) {
        boolean exists = serviceRepository.existsByNameIgnoreCase(serviceName);
        if (exists) {
            throw new BusinessRuleException("Ya existe un servicio con el nombre: " + serviceName);
        }
    }

    // obtener todos
    @Override
    public List<ServiceDto> getAll() {
        return serviceRepository.findAll()
                .stream()
                .map(ServiceMapper::toDto)
                .toList();
    }

    // patch
    @Override
    @Transactional
    public ServiceDto update(UUID id, ServiceUpdateDto request) {

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el servicio con id " + id));

        updateServiceFields(service, request);

        ServiceEntity updatedEntity = serviceRepository.save(service);

        return ServiceMapper.toDto(updatedEntity);
    }

    private void updateServiceFields(ServiceEntity service, ServiceUpdateDto request) {
        if (request.name() != null) {
            if (!service.getName().equalsIgnoreCase(request.name())) {
                validateExistsService(request.name());
            }
            service.setName(request.name());
        }

        if (request.description() != null) {
            service.setDescription(request.description());
        }

        if (request.price() != null) {
            service.setPrice(request.price());
        }

        if (request.category() != null) {
            service.setCategory(request.category());
        }

        if (request.durationMinutes() != null) {
            service.setDurationMinutes(request.durationMinutes());
        }

        if (request.bufferMinutes() != null) {
            service.setBufferMinutes(request.bufferMinutes());
        }

        if (request.isRecommended() != null) {
            service.setRecommended(request.isRecommended());
        }

        if (request.isActive() != null) {
            service.setActive(request.isActive());
        }
    }

    //baja logica
    @Override
    public void deleteLogical(UUID id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el servicio con id " + id));

        if (service.isActive()) {
            service.setActive(false);
            serviceRepository.save(service);
        }
    }
}
