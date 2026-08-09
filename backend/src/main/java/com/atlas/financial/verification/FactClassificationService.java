package com.atlas.financial.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FactClassificationService {

    private static final Logger log = LoggerFactory.getLogger(FactClassificationService.class);

    public enum FactCategory {
        FACT("[FACT - SEC Verified Historical Data]"),
        ESTIMATE("[ESTIMATE - Consensus Analyst Expectation]"),
        FORECAST("[FORECAST - Official Company Forward Guidance]"),
        INFERENCE("[INFERENCE - Derived Market Catalyst Reasoning]");

        private final String tag;

        FactCategory(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    public String appendFactTags(String responseText) {
        if (responseText == null) return "";
        if (responseText.contains("FACT & CLAIM CLASSIFICATION LEGEND:")) {
            return responseText;
        }

        StringBuilder sb = new StringBuilder(responseText);

        sb.append("\n\n🏷️ **FACT & CLAIM CLASSIFICATION LEGEND:**\n");
        sb.append("• **Historical Metrics:** ").append(FactCategory.FACT.getTag()).append("\n");
        sb.append("• **Consensus Figures:** ").append(FactCategory.ESTIMATE.getTag()).append("\n");
        sb.append("• **Forward Targets:** ").append(FactCategory.FORECAST.getTag()).append("\n");
        sb.append("• **Analyst Drivers:** ").append(FactCategory.INFERENCE.getTag());

        return sb.toString();
    }
}
