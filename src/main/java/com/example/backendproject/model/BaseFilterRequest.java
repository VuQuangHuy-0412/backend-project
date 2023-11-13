package com.example.backendproject.model;

public class BaseFilterRequest {
    protected int page;
    protected int pageSize;

    public void setPage(int page) {
        if (page <= 1) {
            page = 1;
        }
        this.page = page - 1; // Pageable spring boot start with index 0
    }

    public void setPageSize(int pageSize) {
        if (pageSize > 100)
            pageSize = 100;
        if (pageSize < 10)
            pageSize = 10;

        this.pageSize = pageSize;
    }

    public void setPageSizeForExport(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageSize(int pageSize, boolean isExport) {
        if(isExport) {
            this.pageSize = pageSize;
        } else {
            setPageSize(pageSize);
        }
    }

    public int getPage() {
        return Math.max(page, 0);
    }

    public int getPageSize() {
        return pageSize > 0 ? pageSize : 100;
    }
}
