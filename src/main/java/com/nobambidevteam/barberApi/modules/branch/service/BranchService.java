package com.nobambidevteam.barberApi.modules.branch.service;

import com.nobambidevteam.barberApi.exceptions.BusinessRuleException;
import com.nobambidevteam.barberApi.modules.branch.dto.BranchCreateDto;
import com.nobambidevteam.barberApi.modules.branch.dto.BranchDto;
import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.branch.mapper.BranchMapper;
import com.nobambidevteam.barberApi.modules.branch.repository.IBranchRepository;
import com.nobambidevteam.barberApi.modules.branch.service.interfaces.IBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {

    private final IBranchRepository branchRepository;


    //--------------------Alta
    @Override
    public BranchDto save(BranchCreateDto request) {

        // Validaciones de Negocio
        validateExistsBranch(request.name());
        validateTimezone(request.timezone());

        // Mapeo de DTO a Entidad
        BranchEntity branchToSave = BranchMapper.toEntity(request);

        // Guardar en Base de Datos
        branchToSave = branchRepository.save(branchToSave);

        // Mapear Entidad a DTO de respuesta
        return BranchMapper.toDto(branchToSave);
    }

    private void validateTimezone(String timezone) {
        if (timezone != null && !timezone.isBlank()) {
            try {
                // Si la zona no existe, Java lanzará una DateTimeException
                java.time.ZoneId.of(timezone);
            } catch (Exception e) {
                // Lanzamos IllegalArgumentException para que tu GlobalExceptionHandler devuelva un 400 Bad Request
                throw new IllegalArgumentException("La zona horaria proporcionada no es válida: " + timezone);
            }
        }
    }

    private void validateExistsBranch(String branchName){
        boolean exists = branchRepository.existsByNameIgnoreCase(branchName);
        if (exists) {
            throw new BusinessRuleException("Ya existe una sucursal con el nombre: " + branchName);
        }
    }


    //-------------------Get All
    @Override
    public List<BranchDto> getAll() {
        return branchRepository.findAll()
                .stream()
                .map(BranchMapper::toDto)
                .toList();
    }

}
