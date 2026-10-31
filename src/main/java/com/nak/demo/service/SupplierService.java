package com.nak.demo.service;

import com.nak.demo.dto.supplier.SupplierResponseDto;
import com.nak.demo.dto.supplier.SupplierUpdateDto;
import com.nak.demo.exception.model.DuplicateException;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.repository.SupplierRepository;
import com.nak.demo.dto.supplier.SupplierDto;
import com.nak.demo.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nak.demo.entity.Supplier;

import java.util.List;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper mapper;

    public List<SupplierResponseDto> listSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();

        return mapper.toDtoList(suppliers);
    }

    public void createSupplier(SupplierDto dto) {
        if(supplierRepository.existsByName(dto.getName())) {
                throw new DuplicateException("supplier already existed");
        }

        Supplier supplier = mapper.toEntity(dto);

        supplierRepository.save(supplier);

    }

    public void updateSupplier(Long supplierId, SupplierUpdateDto dto) {
        Supplier existingSupplier = supplierRepository.findById(supplierId)

                .orElseThrow(() ->
                        new ResourceNotFoundException("supplier not found with :" + supplierId));

        mapper.updateEntityFromDto(existingSupplier, dto);

        supplierRepository.save(existingSupplier);


    }

    public void deleteSupplier(Long supplierId) {
        if(!supplierRepository.existsById(supplierId)) {
           throw new ResourceNotFoundException("supplier not found with id:"  +supplierId);
        }

        supplierRepository.deleteById(supplierId);

    }

}