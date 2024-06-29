package com.thesler.invdbmgr;

import androidx.annotation.NonNull;

public class inventoryItem {
    private int id;
    private String name;
    private int count;

    public inventoryItem(@NonNull int newId){
        this(newId,"undefined",0);
    }

    public inventoryItem(@NonNull int newId, String newName, int newCount){
        this.id = newId;
        this.name = newName;
        this.count = newCount;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getCount(){
        return this.count;
    }
}
