package com.ccsw.tutorial.client.model;

import com.ccsw.tutorial.common.pagination.PageableRequest;

public class ClientSearchDto {

    private PageableRequest pageable;

    public PageableRequest getPageable() {
        return pageable;
    }

    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }

}
