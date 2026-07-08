package pkgBoundary;

import java.util.List;
import java.util.Map;

/**
 * A DTO class that mimics the essential methods of java.sql.ResultSet.
 * This allows the Client Node to iterate over database results received over HTTP (JSON),
 * without needing the actual java.sql.ResultSet object.
 */
public class ResultDto {
    private List<Map<String, Object>> data;
    private int cursor = -1;

    public ResultDto(List<Map<String, Object>> data) {
        this.data = data;
    }

    public boolean next() {
        if (data == null) return false;
        cursor++;
        return cursor < data.size();
    }

    public String getString(String columnLabel) {
        if (data == null || cursor < 0 || cursor >= data.size()) return null;
        Object val = data.get(cursor).get(columnLabel);
        return val != null ? val.toString() : null;
    }
    
    public String getString(int columnIndex) {
        // For JDBC, column index is 1-based.
        if (data == null || cursor < 0 || cursor >= data.size()) return null;
        Map<String, Object> row = data.get(cursor);
        int i = 1;
        for (Object val : row.values()) {
            if (i == columnIndex) return val != null ? val.toString() : null;
            i++;
        }
        return null;
    }

    public int getInt(String columnLabel) {
        if (data == null || cursor < 0 || cursor >= data.size()) return 0;
        Object val = data.get(cursor).get(columnLabel);
        if (val instanceof Number) return ((Number) val).intValue();
        if (val instanceof String) {
            try {
                return Integer.parseInt((String) val);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public int getInt(int columnIndex) {
        String s = getString(columnIndex);
        if (s != null) {
            try {
                return (int) Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public boolean getBoolean(String columnLabel) {
        if (data == null || cursor < 0 || cursor >= data.size()) return false;
        Object val = data.get(cursor).get(columnLabel);
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() == 1;
        if (val instanceof String) {
            return "true".equalsIgnoreCase((String) val) || "1".equals(val);
        }
        return false;
    }
}
