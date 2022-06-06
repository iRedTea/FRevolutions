package me.redtea.factionrevolutions.db.impl.sql.adapter;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public record SQLAdapter() {
    public String listToString(@NotNull List<String> list) {
        return list.stream()
                .collect(Collectors.joining("-", "{", "}"));
    }

    public ArrayList<String> stringToList(String string) {
        string = string.replace("{", "").replace("}", "");
        return (ArrayList<String>) Arrays.asList(string.split("-"));
    }

    public String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public Object deserialize(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
}
