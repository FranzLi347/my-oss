package io.github.franzli347.foss.support.fileSupport;

import cn.hutool.core.util.IdUtil;
import io.github.franzli347.foss.dto.FileUploadParam;
import io.github.franzli347.foss.entity.Files;
import io.github.franzli347.foss.service.FilesService;
import lombok.SneakyThrows;

import java.nio.file.Path;

/**
 *
 *  数据库存储
 * @author FranzLi
 */

public class DbFileUploadPostprocessor implements FileUploadPostProcessor{

    private final FilesService filesService;

    public DbFileUploadPostprocessor(FilesService filesService) {
        this.filesService = filesService;
    }

    @SneakyThrows
    @Override
    public boolean process(String filePath, FileUploadParam param) {
        return filesService.save(
                Files
                .builder()
                .id(IdUtil.getSnowflakeNextId())
                .bid(param.getBid())
                .fileName(param.getName())
                .path(param.getBid() + "/" + param.getName())
                .md5(param.getMd5())
                .fileSize((double) java.nio.file.Files.size(Path.of(filePath)))
                .build()
        );
    }
}
