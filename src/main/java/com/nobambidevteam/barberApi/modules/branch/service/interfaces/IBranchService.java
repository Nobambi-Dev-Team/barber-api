package com.nobambidevteam.barberApi.modules.branch.service.interfaces;

import com.nobambidevteam.barberApi.modules.branch.dto.BranchCreateDto;
import com.nobambidevteam.barberApi.modules.branch.dto.BranchDto;
import com.nobambidevteam.barberApi.modules.branch.dto.BranchUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IBranchService {

    public BranchDto save(BranchCreateDto request);

    List<BranchDto> getAll();

    BranchDto update(UUID id, BranchUpdateDto request);

    void deleteLogical(UUID id);
}
