package app;

import enums.DiningOption;

import java.util.HashMap;
import java.util.Map;

public class TableManager {
    private static final int NUM_TABLES = 25;
    private Map<DiningOption, Map<String, Boolean>> tables;

    public TableManager() {
        tables = new HashMap<>();

        // Initialize the map with empty maps for each dining option
        for (DiningOption option : DiningOption.values()) {
            tables.put(option, new HashMap<>());
        }
    }

    public boolean isTableBusy(DiningOption diningOption, String tableName) {
        Map<String, Boolean> diningOptionTables = tables.get(diningOption);
        return diningOptionTables.getOrDefault(tableName, false);
    }

    public void setTableBusy(DiningOption diningOption, String tableName, boolean isBusy) {
        Map<String, Boolean> diningOptionTables = tables.get(diningOption);

        if (diningOptionTables != null) {
            diningOptionTables.put(tableName, isBusy);
        }
    }

    public DiningOption getDiningOption(String tableName) {
        for (Map.Entry<DiningOption, Map<String, Boolean>> entry : tables.entrySet()) {
            if (entry.getValue().containsKey(tableName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
