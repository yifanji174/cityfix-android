package com.g04.cityfix.common.utils.searchparse;

import com.g04.cityfix.data.model.RepairReport;

import java.util.List;

public class AstOr implements IAstNode {

    private final List<IAstNode> children;
    public AstOr(List<IAstNode> children) {
        this.children = children;
    }
    @Override
    public boolean check(RepairReport report) {
        for (IAstNode child: this.children) {
            if(child.check(report)) {
                return true;
            }
        }
        return false;
    }
}
