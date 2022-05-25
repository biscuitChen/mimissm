package cn.fafu.service.impl;


import cn.fafu.mapper.ProductTypeMapper;
import cn.fafu.pojo.ProductType;
import cn.fafu.pojo.ProductTypeExample;
import cn.fafu.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ProducTypeServiceImpl")
public class ProducTypeServiceImpl implements ProductTypeService {

    @Autowired
    ProductTypeMapper productTypeMapper;

    @Override
    public List<ProductType> getAll() {

        return productTypeMapper.selectByExample(new ProductTypeExample());
    }
}
