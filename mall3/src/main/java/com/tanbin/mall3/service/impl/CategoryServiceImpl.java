package com.tanbin.mall3.service.impl;

import com.tanbin.mall3.dao.CategoryMapper;
import com.tanbin.mall3.pojo.Category;
import com.tanbin.mall3.service.ICategoryService;
import com.tanbin.mall3.vo.CategoryVo;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tanbin.mall3.consts.MallConst.ROOT_PARENT_ID;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        //1、先查出所有类目,并查出其中parent_id=0的数据
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParentId().equals(ROOT_PARENT_ID)){
                CategoryVo categoryVo = category2CategoryVo(category);
                categoryVoList.add(categoryVo);
            }
        }
        //一级目录排序
        categoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
        //也可以使用   lambda  +  stream
//        List<CategoryVo> categoryVoList = categories.stream()
//                .filter(e -> e.getParentId().equals(ROOT_PARENT_ID))
//                .map(this::category2CategoryVo)
//                .collect(Collectors.toList());

        //2、查询子目录
        findSubCategory(categoryVoList,categories);

        return ResponseVo.success(categoryVoList);
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id,resultSet,categories);
    }
    //方法的重载
    public void findSubCategoryId(Integer id,Set<Integer> resultSet,List<Category> categories){
        for (Category category : categories) {
            if (category.getParentId().equals(id)){
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(),resultSet,categories);
            }
        }
    }

    public void findSubCategory(List<CategoryVo> categoryVoList,List<Category> categories){
        //先拿到上一级的id，再去categories中查找parent_id=id的数据，两次遍历
        for (CategoryVo categoryVo : categoryVoList) {
            ArrayList<CategoryVo> subCategoryVoList = new ArrayList<>();
            for (Category category : categories) {
                //如果查到内容，要设置SubCategory，并且继续往下查
                if (categoryVo.getId().equals(category.getParentId())){
                    CategoryVo subCategoryVo = category2CategoryVo(category);
                    subCategoryVoList.add(subCategoryVo);
                }
            }
            //排序
            subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
            categoryVo.setSubCategories(subCategoryVoList);
            //继续递归调用，继续查询下一级目录
            findSubCategory(subCategoryVoList,categories);
        }
    }

    //category对象 --> CategoryVo对象
    private CategoryVo category2CategoryVo(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}
