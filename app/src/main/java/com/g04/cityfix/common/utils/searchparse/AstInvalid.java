package com.g04.cityfix.common.utils.searchparse;

import com.g04.cityfix.data.model.RepairReport;

public class AstInvalid implements IAstNode {
    @Override
    public boolean check(RepairReport report) {
        return false;
    }
}
