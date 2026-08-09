package com.atlas.financial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "watchlists", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "ticker"})
})
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String ticker;

    public Watchlist() {}

    public Watchlist(Long id, Long userId, String ticker) {
        this.id = id;
        this.userId = userId;
        this.ticker = ticker;
    }

    public static WatchlistBuilder builder() {
        return new WatchlistBuilder();
    }

    public static class WatchlistBuilder {
        private Long id;
        private Long userId;
        private String ticker;

        public WatchlistBuilder id(Long id) { this.id = id; return this; }
        public WatchlistBuilder userId(Long userId) { this.userId = userId; return this; }
        public WatchlistBuilder ticker(String ticker) { this.ticker = ticker; return this; }

        public Watchlist build() {
            return new Watchlist(id, userId, ticker);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }
}
