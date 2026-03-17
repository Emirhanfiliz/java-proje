package com.sporttracker.shared.entity;

import java.io.Serializable;

/**
 * Projedeki tüm Entity (Veritabanı tabloları/koleksiyonları) sınıflarının kalıtım alacağı temel Generic Interface.
 * ID tipini jenerik (ID) olarak alır (Örn: MongoDB için String, PostgreSQL için Long).
 */
public interface BaseEntity<ID extends Serializable> extends Serializable {
    ID getId();
    void setId(ID id);
}
