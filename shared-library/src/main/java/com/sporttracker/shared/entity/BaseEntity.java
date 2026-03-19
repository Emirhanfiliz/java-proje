package com.sporttracker.shared.entity;

import java.io.Serializable;

/**
 * Projedeki tüm Entity  sınıflarının kalıtım alacağı temel Generic Interface.
 * ID tipini jenerik (ID) olarak alır 
 */
public interface BaseEntity<ID extends Serializable> extends Serializable {
    ID getId();
    void setId(ID id);
}
