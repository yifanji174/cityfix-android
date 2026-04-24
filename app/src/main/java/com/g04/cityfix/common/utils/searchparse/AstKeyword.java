package com.g04.cityfix.common.utils.searchparse;

import com.g04.cityfix.data.model.RepairReport;

public class AstKeyword implements IAstNode {
    private final String keyword;
    
    public AstKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean check(RepairReport report) {
        String title = report.getTitle().toLowerCase();
        String description = report.getDescription().toLowerCase();
        String username = report.getCitizenUsername().toLowerCase();
        return (title.contains(keyword)
                || description.contains(keyword)
                || username.contains(keyword));
    }
}
