package me.redtea.factionrevolutions.db.impl.sql;

public interface ResponseHandler <V, T extends Throwable> {

    void handle(V value) throws T;
}