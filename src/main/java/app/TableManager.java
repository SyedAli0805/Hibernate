package app;

import enums.DiningOption;

import java.util.HashMap;
import java.util.Map;

public class TableManager {
    private static final int NUM_TABLES = 25;
    private Map<DiningOption, Map<String, Boolean>> tables;

    public TableManager() {
        tables = new HashMap<>();
        for (DiningOption option : DiningOption.values()) {
            Map<String, Boolean> tableMap = new HashMap<>();
            for (int i = 1; i <= NUM_TABLES; i++) {
                tableMap.put(option.name() + "_Table_" + i, false); // false indicates the table is not busy
            }
            tables.put(option, tableMap);
        }
    }

    public boolean isTableBusy(DiningOption option, String tableName) {
        return tables.get(option).get(tableName);
    }

    public void setTableBusy(DiningOption option, String tableName, boolean isBusy) {
        tables.get(option).put(tableName, isBusy);
    }

    // Additional methods to manage tables can be added here
}
