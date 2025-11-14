package com.example.develop.service;

import com.example.develop.DTO.MerchantDTO;
import com.example.develop.DTO.MerchantFilterAndSortRequest;
import com.example.develop.entity.Merchant;

import java.util.List;

public interface MerchantSorting {
    public List<MerchantDTO> sortMerchant(List<MerchantDTO> merchants, MerchantFilterAndSortRequest request);
    public List<Merchant> sortByRating(List<Merchant> merchants);
    public List<Merchant> sortByPrice(List<Merchant> merchants);
    public List<Merchant> sortByOverall(List<Merchant> merchants);
    public List<MerchantDTO> convertToDTOs(List<Merchant> merchants);
    public MerchantDTO convertToDTO(Merchant merchant);
    public List<Merchant> convertToEntities(List<MerchantDTO> merchantDTOs);
    public Merchant convertToEntity(MerchantDTO merchantDTO);
}
