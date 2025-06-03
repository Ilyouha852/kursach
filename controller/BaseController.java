package controller;

import java.util.List;

public interface BaseController<T> {
    List<T> getAll();
    T getById(int id);
    void save(T item);
    void update(T item);
    void delete(int id);
} 