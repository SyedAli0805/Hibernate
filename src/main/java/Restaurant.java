import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private List<Table> tables;

    public Restaurant() {
        tables = new ArrayList<>();
        initializeTables();
    }

    private void initializeTables() {
        for (DiningOption option : DiningOption.values()) {
            for (int i = 1; i <= 25; i++) {
                String tableName = option.name().charAt(0) + "-" + i;
                tables.add(new Table(tableName, option));
            }
        }
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<Table> getTablesByDiningOption(DiningOption option) {
        List<Table> result = new ArrayList<>();
        for (Table table : tables) {
            if (table.getOption() == option) {
                result.add(table);
            }
        }
        return result;
    }

    public void changeTableState(Table table, boolean isBusy) {
        table.setBusy(isBusy);
    }

    public void releaseTable(Table table) {
        table.setBusy(false);
    }
}
