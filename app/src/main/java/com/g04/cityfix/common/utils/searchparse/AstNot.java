package com.g04.cityfix.common.utils.searchparse;

import com.g04.cityfix.data.model.RepairReport;

public class AstNot implements IAstNode {
    private final IAstNode node;
    public AstNot(IAstNode node) {
        this.node = node;
    }

    @Override
    public boolean check(RepairReport report) {
        return !node.check(report);
    }
}
