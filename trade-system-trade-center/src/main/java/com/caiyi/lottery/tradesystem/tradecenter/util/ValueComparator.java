package com.caiyi.lottery.tradesystem.tradecenter.util;

import java.util.Comparator;
import java.util.Map;


public class ValueComparator implements Comparator<String> {
    Map<String, Double> base;
    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    @Override
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}
