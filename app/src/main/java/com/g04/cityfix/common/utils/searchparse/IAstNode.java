package com.g04.cityfix.common.utils.searchparse;

import com.g04.cityfix.data.model.RepairReport;

public interface IAstNode {
    boolean check(RepairReport report);
}
