package net.jacobpeterson.alpaca;

/**
 * {@link AlpacaConstants} are constants use by {@link AlpacaAPI}.
 */
public class AlpacaConstants {

    /**
     * Defines URLs of {@link AlpacaAPI}
     */
    public static class URLs {

        public static final String DATA = "https://data.alpaca.markets";
    }

    /**
     * Defines endpoints of {@link AlpacaAPI}.
     */
    public static class Endpoints {

        public static final String VERSION_2 = "v2";
        public static final String STOCKS = "stocks";
        public static final String CLOCK = "clock";
        public static final String CALENDAR = "calendar";
        public static final String ASSETS = "assets";
        public static final String POSITIONS = "positions";
        public static final String ACCOUNT = "account";
        public static final String ACTIVITIES = "activities";
        public static final String CONFIGURATIONS = "configurations";
        public static final String ORDERS_BY_CLIENT_ORDER_ID = "orders:by_client_order_id";
        public static final String ORDERS = "orders";
        public static final String WATCHLISTS = "watchlists";
        public static final String PORTFOLIO = "portfolio";
        public static final String HISTORY = "history";
        public static final String TRADES = "trades";
        public static final String QUOTES = "quotes";
        public static final String BARS = "bars";
    }

    /**
     * Defines parameters of {@link AlpacaAPI}.
     */
    public static class Parameters {

        public static final String SYMBOLS = "symbols";
        public static final String END = "end";
        public static final String START = "start";
        public static final String DATE = "date";
        public static final String ASSET_CLASS = "asset_class";
        public static final String CLIENT_ORDER_ID = "client_order_id";
        public static final String ORDER_CLASS = "order_class";
        public static final String TAKE_PROFIT = "take_profit";
        public static final String STOP_LOSS = "stop_loss";
        public static final String STOP_PRICE = "stop_price";
        public static final String TRAIL_PRICE = "trail_price";
        public static final String TRAIL_PERCENT = "trail_percent";
        public static final String TRAIL = "trail";
        public static final String LIMIT_PRICE = "limit_price";
        public static final String TIME_IN_FORCE = "time_in_force";
        public static final String EXTENDED_HOURS = "extended_hours";
        public static final String TYPE = "type";
        public static final String SIDE = "side";
        public static final String QTY = "qty";
        public static final String SYMBOL = "symbol";
        public static final String DIRECTION = "direction";
        public static final String NESTED = "nested";
        public static final String UNTIL = "until";
        public static final String AFTER = "after";
        public static final String LIMIT = "limit";
        public static final String STATUS = "status";
        public static final String PERIOD = "period";
        public static final String TIMEFRAME = "timeframe";
        public static final String DATE_END = "date_end";
        public static final String PAGE_SIZE = "page_size";
        public static final String PAGE_TOKEN = "page_token";
    }
}
