package com.atlas.financial.calculation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FinancialCalculationEngine {

    private static final Logger log = LoggerFactory.getLogger(FinancialCalculationEngine.class);

    public double calculateYoYGrowth(double currentVal, double previousVal) {
        if (previousVal == 0) return 0.0;
        return ((currentVal - previousVal) / Math.abs(previousVal)) * 100.0;
    }

    public double calculateMarginBps(double currentMarginPercent, double previousMarginPercent) {
        return (currentMarginPercent - previousMarginPercent) * 100.0;
    }

    public double calculateCAGR(double startVal, double endVal, int numYears) {
        if (startVal <= 0 || numYears <= 0) return 0.0;
        return (Math.pow(endVal / startVal, 1.0 / numYears) - 1.0) * 100.0;
    }

    public double calculatePeRatio(double stockPrice, double eps) {
        if (eps <= 0) return 0.0;
        return stockPrice / eps;
    }
}
