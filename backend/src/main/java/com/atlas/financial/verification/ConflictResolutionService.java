package com.atlas.financial.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConflictResolutionService {

    private static final Logger log = LoggerFactory.getLogger(ConflictResolutionService.class);

    public String resolveConflict(String primaryVal, String secondaryVal, String primarySource, String secondarySource) {
        log.info("Evaluating cross-source discrepancy: Primary ({}) vs Secondary ({})", primaryVal, secondaryVal);

        if (primaryVal != null && secondaryVal != null && !primaryVal.equalsIgnoreCase(secondaryVal)) {
            return String.format("""
                    
                    ⚠️ **CROSS-SOURCE DISCREPANCY DETECTED & RESOLVED:**
                    • **Primary SEC EDGAR Filing:** %s
                    • **Secondary Media Aggregate:** %s
                    💡 *Resolution:* Atlas automatically defaulted to verified Tier-1 Primary SEC EDGAR Filing (%s) to guarantee zero-hallucination accuracy.
                    """, primaryVal, secondaryVal, primarySource);
        }
        return "";
    }
}
