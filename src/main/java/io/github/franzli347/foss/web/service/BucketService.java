package io.github.franzli347.foss.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.franzli347.foss.model.vo.BucketWithPriviledge;
import io.github.franzli347.foss.model.entity.Bucket;

import java.util.List;

/**
* @author FranzLi
* @description 针对表【tb_bucket】的数据库操作Service
* @createDate 2022-12-15 10:46:45
*/
public interface BucketService extends IService<Bucket> {

    List<Bucket> getBucketsByUserIdWithPage(int userId,int page,int size);

    IPage<BucketWithPriviledge> listAll(int userId, int page, int size);

    boolean updateBucketSizeWithFile(Integer bid, double fileSize);

    boolean removeBucket(int id);

    boolean updateBucketData(Bucket bucket);

    IPage<Bucket> getLoginUserBucket(int userId, int page, int size);
}
