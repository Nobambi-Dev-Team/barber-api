package com.nobambidevteam.barberApi.modules.branch.controller;

import com.nobambidevteam.barberApi.modules.branch.dto.BranchCreateDto;
import com.nobambidevteam.barberApi.modules.branch.dto.BranchDto;
import com.nobambidevteam.barberApi.modules.branch.service.interfaces.IBranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final IBranchService branchService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('BRANCH_MANAGE')")
    public BranchDto createBranch(@Valid @RequestBody BranchCreateDto request){
        return branchService.save(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BranchDto> getAllBranches(){
        return branchService.getAll();
    }

}
