package com.example.backendproject.repository.custom;

import com.example.backendproject.entity.FileEntity;
import com.example.backendproject.model.FileExportFilter;

import java.util.List;

public interface FileRepositoryCustom {

    List<FileEntity> findAllByFilter(FileExportFilter filter);

    Long findTotalCountByFilter(FileExportFilter filter);
}
