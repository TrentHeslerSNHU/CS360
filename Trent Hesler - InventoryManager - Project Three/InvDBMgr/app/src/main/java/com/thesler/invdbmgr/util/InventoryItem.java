package com.thesler.invdbmgr.util;

import androidx.annotation.NonNull;

public class InventoryItem {
    private int itemId;
    private String itemName;
    private int itemCount;

    public InventoryItem(@NonNull int newId){
        this(newId,"undefined",0);
    }
    public InventoryItem(@NonNull int newId, String newName, int newCount){
        this.itemId = newId;
        this.itemName = newName;
        this.itemCount = newCount;
    }

    public int getId(){
        return this.itemId;
    }

    public String getName(){
        return this.itemName;
    }

    public int getCount(){
        return this.itemCount;
    }

    public void setId(int newId){
        this.itemId = newId;
    }

    public void setName(String newName){
        this.itemName = newName;
    }

    public void setCount(int newCount){
        this.itemCount = newCount;
    }
}
