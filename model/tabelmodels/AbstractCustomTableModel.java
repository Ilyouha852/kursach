package model.tabelmodels;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCustomTableModel<T> extends AbstractTableModel implements CustomTableModel<T> {
    protected List<T> data;
    protected String[] columnNames;

    public AbstractCustomTableModel(String[] columnNames) {
        this.columnNames = columnNames;
        this.data = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public void setData(List<T> data) {
        this.data = new ArrayList<>(data);
        fireTableDataChanged();
    }

    @Override
    public List<T> getData() {
        return new ArrayList<>(data);
    }

    @Override
    public T getItemAt(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    public void addRow(T item) {
        data.add(item);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    @Override
    public void removeRow(int rowIndex) {
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    @Override
    public void updateRow(int rowIndex, T item) {
        data.set(rowIndex, item);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    @Override
    public void clear() {
        int size = data.size();
        data.clear();
        fireTableRowsDeleted(0, size - 1);
    }
} 