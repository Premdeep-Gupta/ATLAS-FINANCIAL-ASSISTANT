package com.atlas.financial.research;

import com.atlas.financial.service.FinancialService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResearchEngine {

    private final FinancialService financialService;

    public ResearchEngine(FinancialService financialService) {
        this.financialService = financialService;
    }

    public Map<String, Object> generateCompanyResearch(String ticker) {
        Map<String, Object> info = financialService.getCompanyInfo(ticker);
        Map<String, Object> research = new HashMap<>(info);

        research.put("bullCase", List.of(
                "Market leadership in high-growth AI compute platforms.",
                "Expanding operating margins driven by high-margin software subscriptions.",
                "Robust free cash flow generation backing aggressive R&D expansion."
        ));

        research.put("bearCase", List.of(
                "Elevated valuation multiples leaving narrow room for earnings execution misses.",
                "Potential macroeconomic rate headwinds pressuring tech sector multiples.",
                "Geopolitical supply chain concentration in foundry manufacturing."
        ));

        research.put("keyUnknowns", List.of(
                "Long-term customer CapEx sustainability.",
                "Regulatory export restrictions in secondary global markets."
        ));

        return research;
    }

    public String formatPeerComparison(String ticker1, String ticker2) {
        Map<String, Object> info1 = financialService.getCompanyInfo(ticker1);
        Map<String, Object> info2 = financialService.getCompanyInfo(ticker2);

        return String.format("""
                📊 **Comparative Financial Investment Analysis: %s vs %s**
                
                ### 1. Key Metrics & Valuation
                • **%s**: Price $%s (%s%%) | P/E: %s | Market Cap: %s | Revenue: %s
                • **%s**: Price $%s (%s%%) | P/E: %s | Market Cap: %s | Revenue: %s
                
                ### 2. Bull / Bear Positioning
                • **%s (Bull Case):** Market leadership and high operating leverage.
                • **%s (Bull Case):** Agile execution and expanding TAM in enterprise solutions.
                
                ### 3. Bottom Line
                %s demonstrates stronger near-term margin expansion, whereas %s offers upside potential relative to historical valuation multiples.
                """,
                ticker1, ticker2,
                ticker1, info1.get("price"), info1.get("percent_change"), info1.get("pe_ratio"), info1.get("market_cap"), info1.get("revenue"),
                ticker2, info2.get("price"), info2.get("percent_change"), info2.get("pe_ratio"), info2.get("market_cap"), info2.get("revenue"),
                ticker1, ticker2, ticker1, ticker2);
    }
}
