package com.nobambidevteam.barberApi.modules.branch.service.interfaces;

import com.nobambidevteam.barberApi.modules.branch.dto.BranchCreateDto;
import com.nobambidevteam.barberApi.modules.branch.dto.BranchDto;

public interface IBranchService {

    public BranchDto save(BranchCreateDto request);

}
