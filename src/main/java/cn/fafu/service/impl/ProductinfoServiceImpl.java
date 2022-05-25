package cn.fafu.service.impl;

import cn.fafu.mapper.ProductInfoMapper;
import cn.fafu.pojo.ProductInfo;
import cn.fafu.pojo.ProductInfoExample;
import cn.fafu.pojo.vo.ProductInfoVo;
import cn.fafu.service.ProductinfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductinfoServiceImpl implements ProductinfoService {

    //业务逻辑层一定有数据访问层对象
    @Autowired
    ProductInfoMapper productInfoMapper;

    @Override
    public List<ProductInfo> getAll() {

        return productInfoMapper.selectByExample(new ProductInfoExample());
    }

    @Override
    public PageInfo splitPage(int pageNum, int pageSize) {

        //分页查询使用PageHelper工具类完成分页额处理
        PageHelper.startPage(pageNum,pageSize);

        //进行PageInfo的数据封装
        //进行有条件的查询操作，必须创建ProductInfoExample对象
        ProductInfoExample productInfoExamplexample = new ProductInfoExample();
        //设置排序，按主键降序
        productInfoExamplexample.setOrderByClause("p_id desc");
        //设置完排序后，取集合，一定要在去集合前，设置PageHelper.startPage(pageNum,pageSize)
        List<ProductInfo> list = productInfoMapper.selectByExample(productInfoExamplexample);
        //将查到的集合封装到PageInfo对象中
        PageInfo<ProductInfo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override

    public int save(ProductInfo info) {

        return productInfoMapper.insert(info);
    }

    @Override
    public ProductInfo getByID(int pid) {
        return productInfoMapper.selectByPrimaryKey(pid);
    }

    @Override
    public int update(ProductInfo info) {
        return productInfoMapper.updateByPrimaryKey(info);
    }

    @Override
    public int delete(int pid) {
        return productInfoMapper.deleteByPrimaryKey(pid);
    }

    @Override
    public int deleteBatch(String[] pid) {

        return productInfoMapper.deleteBatch(pid);
    }

    @Override
    public List<ProductInfo> selectCondition(ProductInfoVo vo) {
        return productInfoMapper.selectCondition(vo);
    }

    @Override
    public PageInfo<ProductInfo> splitPageVo(ProductInfoVo vo, int pageSize) {

        PageHelper.startPage(vo.getPage(), pageSize);
        List<ProductInfo> list = productInfoMapper.selectCondition(vo);

        return new PageInfo<>(list);
    }
}
