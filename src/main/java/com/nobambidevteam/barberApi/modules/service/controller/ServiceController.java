package com.nobambidevteam.barberApi.modules.service.controller;

import com.nobambidevteam.barberApi.modules.service.dto.ServiceCreateDto;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceDto;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceUpdateDto;
import com.nobambidevteam.barberApi.modules.service.service.interfaces.IServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final IServiceService serviceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SERVICE_MANAGE')")
    public ServiceDto createService(@Valid @RequestBody ServiceCreateDto request) {
        return serviceService.save(request);
    }

    /*
     * @GetMapping
     * 
     * @ResponseStatus(HttpStatus.OK)
     * public List<ServiceDto> getAllServices() {
     * return serviceService.getAll();
     * }
     */

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        List<ServiceDto> services = serviceService.getAll();
        if (services.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(services);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SERVICE_MANAGE')")
    public ServiceDto updateService(@PathVariable UUID id,
            @Valid @RequestBody ServiceUpdateDto request) {
        return serviceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SERVICE_MANAGE')")
    public void deleteService(@PathVariable UUID id) {
        serviceService.deleteLogical(id);
    }
}
