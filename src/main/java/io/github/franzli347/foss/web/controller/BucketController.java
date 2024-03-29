package io.github.franzli347.foss.web.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.franzli347.foss.annotation.CheckBucketPrivilege;
import io.github.franzli347.foss.annotation.FiledExistInTable;
import io.github.franzli347.foss.common.constant.AuthConstant;
import io.github.franzli347.foss.model.dto.bucketDto.BucketCreateDto;
import io.github.franzli347.foss.model.dto.bucketDto.BucketUpdateDto;
import io.github.franzli347.foss.model.entity.Bucket;
import io.github.franzli347.foss.model.entity.BucketPrivilege;
import io.github.franzli347.foss.model.vo.BucketWithPriviledge;
import io.github.franzli347.foss.support.userSupport.LoginUserProvider;
import io.github.franzli347.foss.web.service.BucketPrivilegeService;
import io.github.franzli347.foss.web.service.BucketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author FranzLi
 */
@RequestMapping("/bucket")
@RestController
@Validated
@Tag(name = "桶管理模块")
public class BucketController {
    private final BucketService bucketService;
    private final LoginUserProvider loginUserProvider;
    private final BucketPrivilegeService bucketPrivilegeService;

    public BucketController(BucketService bucketService, LoginUserProvider loginUserProvider, BucketPrivilegeService bucketPrivilegeService) {
        this.bucketService = bucketService;
        this.loginUserProvider = loginUserProvider;
        this.bucketPrivilegeService = bucketPrivilegeService;
    }

    @PostMapping("list/{page}/{size}")
    @Operation(summary = "获取登录用户的所有bucket")
    public IPage<Bucket> list(@PathVariable @Parameter(name = "page", description = "页码") int page,
                              @PathVariable @Parameter(name = "size", description = "每页数量") int size) {
        int userId = Optional.ofNullable(loginUserProvider.getLoginUser())
                .orElseThrow(() -> new RuntimeException("loginUserProvider_exception"))
                .getId();

        return bucketService.getLoginUserBucket(userId, page, size);
    }


    @PostMapping("listAll/{page}/{size}")
    @Operation(summary = "获取登录用户的所有有权限的bucket")
    public IPage<BucketWithPriviledge> listAll(@PathVariable @Parameter(name = "page", description = "页码") int page,
                                               @PathVariable @Parameter(name = "size", description = "每页数量") int size) {
        return bucketService.listAll(Optional
                .ofNullable(loginUserProvider.getLoginUser())
                .orElseThrow(() -> new RuntimeException("loginUserProvider exception")).getId(), page, size);
    }


    @DeleteMapping("delete/{bid}")
    @Operation(summary = "删除bucket")
    @CheckBucketPrivilege(spelString = "#bid", argType = AuthConstant.BID, privilege = {AuthConstant.OWNER})
    @Transactional
    public boolean delete(@PathVariable int bid) {
        return bucketService.removeBucket(bid);
    }

    @PutMapping
    @Operation(summary = "为当前登录用户创建bucket")
    @Transactional
    public boolean create(@RequestBody @Validated BucketCreateDto bucketCreateDto) {
        Bucket bucket = new Bucket();

        bucket.setName(bucketCreateDto.getName());
        bucket.setTotalSize(bucketCreateDto.getTotalSize());
        bucket.setUid(Optional
                .ofNullable(loginUserProvider.getLoginUser())
                .orElseThrow(() -> new RuntimeException("loginUserProvider exception"))
                .getId());

        bucket.setUsedSize(0.0);

        boolean success = bucketService.save(bucket);

        bucketPrivilegeService.save(BucketPrivilege
                .builder().bid(bucket.getId())
                .uid(StpUtil.getLoginIdAsInt())
                .privilege(AuthConstant.READWRITE)
                .build());

        return success;

    }

    @PostMapping("/update")
    @Operation(summary = "更新bucket信息")
    public boolean update(@RequestBody @Validated BucketUpdateDto bucketUpdateDto) {
        Bucket bucket = new Bucket();
        bucket.setId(bucketUpdateDto.getId());
        bucket.setName(bucket.getName());
        bucket.setTotalSize(bucket.getTotalSize());
        return bucketService.updateBucketData(bucket);
    }

    @GetMapping("/get/{bid}")
    @Operation(summary = "获取bucket信息")
    @Parameter(name = "bid", description = "bucket id")
    @CheckBucketPrivilege(spelString = "#bid", argType = AuthConstant.BID, privilege = {AuthConstant.ONLYREAD, AuthConstant.OWNER, AuthConstant.READWRITE})
    public Bucket get(@PathVariable @FiledExistInTable(colum = "id", serviceClz = BucketService.class, message = "bucket id不存在") int bid) {
        return bucketService.getById(bid);
    }

    @GetMapping("/get/count")
    @Operation(summary = "获取当前用户bucket总数")
    public String getCount() {
        return String.valueOf(bucketPrivilegeService.count(new LambdaQueryWrapper<BucketPrivilege>().eq(BucketPrivilege::getUid, StpUtil.getLoginIdAsInt())));
    }

}
