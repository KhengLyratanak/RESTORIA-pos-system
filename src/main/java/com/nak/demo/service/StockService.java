package com.nak.demo.service;

import com.nak.demo.entity.Product;
import com.nak.demo.entity.Stock;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.repository.ProductRepository;
import com.nak.demo.dto.stock.StockDto;
import com.nak.demo.dto.stock.UpdateStockDto;
import com.nak.demo.repository.StockRepository;
import com.nak.demo.dto.stock.StockResponseDto;
import com.nak.demo.exception.model.UnprocessableEntityException;
import com.nak.demo.mapper.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockMapper mapper;

    @Autowired
    private ProductRepository productRepository;

    public List<StockResponseDto> listStocks() {
        List<Stock> stocks = stockRepository.findAll();
        List<StockResponseDto> dtos = mapper.toDtoList(stocks);

        return mapper.toDtoList(stocks);
    }
    public StockResponseDto getStock(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("stock not found with id :" + stockId));
        return mapper.toDto(stock);
    }


    public void  createdStocks(StockDto stock) {
        Product existingProduct = productRepository.findById(stock.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("product not found with id:" + stock.getProductId()));

        Stock stockEntity = mapper.toEntity(stock, existingProduct);
        stockRepository.save(stockEntity);
    }

    public void adjustQuantity(Long stockId, UpdateStockDto updateStock) {
        Stock existingStock = stockRepository.findById(stockId)

                .orElseThrow(() ->
                        new ResourceNotFoundException("stock not found with id :"  +stockId));

        if(updateStock.getOperationType() == 1) {
            Long newQty = existingStock.getQuantity() + updateStock.getQuantity();

            existingStock.setQuantity(newQty);
        } else if(updateStock.getOperationType() == 2) {
            if(existingStock.getQuantity() < updateStock.getQuantity()) {
                throw new UnprocessableEntityException("quantity to remove can not be exceeded than existing stock:" + existingStock.getQuantity());
            }
            Long newQty = existingStock.getQuantity() - updateStock.getQuantity();

            existingStock.setQuantity(newQty);
        } else {
            throw new ResourceNotFoundException("Invalid operation type");
        }

        stockRepository.save(existingStock);

    }
        public void deletedStock(Long stockId) {
            if (!stockRepository.existsById(stockId)) {
                throw new ResourceNotFoundException("stock not found with id : " + stockId);
            }
            stockRepository.deleteById(stockId);
        }
}