package com.example.backendproject.repository.custom;

import vn.ghtk.ewallet.admin.entity.FileEntity;
import vn.ghtk.ewallet.admin.model.admin.FileExportFilter;

import java.util.List;

public interface FileRepositoryCustom {

    List<FileEntity> findAllByFilter(FileExportFilter filter);

    Long findTotalCountByFilter(FileExportFilter filter);
}
