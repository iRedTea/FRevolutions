package me.redtea.factionrevolutions.db.impl.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.redtea.factionrevolutions.types.impl.Revolution;
import me.redtea.factionrevolutions.types.Role;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.Map;

public class JsonAdapter extends TypeAdapter<Object> {

    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        if(value == null) {
            out.nullValue();
        } else {
            if(value instanceof Revolution) {
                Revolution revolution = (Revolution) value;

                out.beginObject();

                out.name("id").value(revolution.getId());

                out.name("leader").value(revolution.getLeader());

                JsonWriter rolesWriter = out.name("roles").beginObject();

                for(Map.Entry<String, Role> roleEntry : revolution.getRoles().entrySet()) {
                    rolesWriter.name(roleEntry.getKey()).value(roleEntry.getValue().name());
                }



//                out.name("x").value(value.getX());
//                out.name("y").value(value.getY());
//                out.name("z").value(value.getZ());
//
//                out.name("yaw").value(value.getYaw());
//                out.name("pitch").value(value.getPitch());

                out.endObject();
            }


        }
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {

            in.beginObject();

            World world = null;
            double x = 0, y = 0, z = 0;
            float yaw = 0, pitch = 0;

            while(in.hasNext()) {
                JsonToken token = in.peek();

                if(token == JsonToken.NAME) {
                    String name = in.nextName();

                    switch (name) {
                        case "world" :
                            world = Bukkit.getWorld(in.nextString());
                            break;
                        case "x" :
                            x = in.nextDouble();
                            break;
                        case "y" :
                            y = in.nextDouble();
                            break;
                        case "z" :
                            z = in.nextDouble();
                            break;
                        case "yaw" :
                            yaw = in.nextLong();
                            break;
                        case "pitch" :
                            pitch = in.nextLong();
                            break;
                    }
                }
            }

            in.endObject();

            return new Location(world, x, y, z, yaw, pitch);
        }
    }
}
