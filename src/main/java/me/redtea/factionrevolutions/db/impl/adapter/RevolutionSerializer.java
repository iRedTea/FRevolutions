package me.redtea.factionrevolutions.db.impl.adapter;

import com.google.common.reflect.*;
import com.google.gson.*;
import lombok.*;
import me.redtea.factionrevolutions.types.*;
import me.redtea.factionrevolutions.types.impl.*;

import java.lang.reflect.*;
import java.util.*;

public class RevolutionSerializer implements JsonSerializer<Revolution>, JsonDeserializer<Revolution> {

    @Override
    public Revolution deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        if(jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String id = null, leader = null;
            HashMap<String, Role> roles = null;
            ArrayList<String> members = null;

            if(jsonObject.has("id")) {
                id = jsonObject.get("id").getAsString();
            }

            if(jsonObject.has("leader")) {
                leader = jsonObject.get("leader").getAsString();
            }

            if(jsonObject.has("roles")) {
                val token = new TypeToken<Map<String, Role>>() {};

                roles = context.deserialize(jsonObject.getAsJsonPrimitive("roles"), token.getType());
            }

            if(jsonObject.has("members")) {
                val token = new TypeToken<List<String>>() {};

                members = context.deserialize(jsonObject.getAsJsonPrimitive("members"), token.getType());
            }

            return new Revolution(UUID.fromString(id), leader, roles, members);
        }
        return null;
    }

    @Override
    public JsonElement serialize(Revolution revolution, Type type, JsonSerializationContext context) {
        if(type != null) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("id", revolution.getId().toString());
            jsonObject.addProperty("leader", revolution.getLeader());

            jsonObject.add("roles", context.serialize(revolution.getRoles()));
            jsonObject.add("members", context.serialize(revolution.getMembers()));

            return jsonObject;
        }
        return null;
    }
}
