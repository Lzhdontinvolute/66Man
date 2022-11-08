package com.lzh.financial.code.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.entity.Atype;
import com.lzh.financial.code.service.AtypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (Atype)表控制层
 *
 * @author makejava
 * @since 2022-10-20 19:48:38
 */
@RestController
@RequestMapping("accountType")
@Api(tags = "账户类型")
public class AtypeController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private AtypeService atypeService;


    @GetMapping("/listAllAccountType")
    @ApiOperation("查询全部账户类型")
    public ResponseResult listAllAccountType() {
        return atypeService.getAllType();
    }

    @GetMapping("/listBaseAccountType")
    @ApiOperation("查询全部账户类型")
    public ResponseResult listBaseAccountType() {
        return atypeService.listBaseAccountType();
    }

    @GetMapping("/queryAccountTypeTree")
    public ResponseResult queryAccountTypeTree(){
        return atypeService.queryAccountTypeTree();
    }




    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.atypeService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param atype 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody Atype atype) {
        return success(this.atypeService.save(atype));
    }

    /**
     * 修改数据
     *
     * @param atype 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody Atype atype) {
        return success(this.atypeService.updateById(atype));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.atypeService.removeByIds(idList));
    }
}

