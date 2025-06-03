package model.tabelmodels;

import javax.swing.table.TableModel;
import java.util.List;

public interface CustomTableModel<T> extends TableModel {
    void setData(List<T> data);
    List<T> getData();
    T getItemAt(int rowIndex);
    void addRow(T item);
    void removeRow(int rowIndex);
    void updateRow(int rowIndex, T item);
    void clear();
} 