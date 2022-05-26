package me.redtea.factionrevolutions.db.impl.json.adapter;

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
            Phase phase = null;
            int points = 0;
            double balance = 0.0d;
            ArrayList<String> sponsoredFactions = new ArrayList<>();

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

            if(jsonObject.has("phase")) {
                phase = Phase.valueOf(jsonObject.get("phase").getAsString());
            }

            if(jsonObject.has("points")) {
                points = jsonObject.get("points").getAsInt();
            }

            if(jsonObject.has("balance")) {
                balance = jsonObject.get("balance").getAsDouble();
            }

            if(jsonObject.has("sponsoredFactions")) {
                val token = new TypeToken<List<String>>() {};

                sponsoredFactions = context.deserialize(jsonObject.getAsJsonPrimitive("sponsoredFactions"), token.getType());
            }

            return new Revolution(UUID.fromString(id), leader, roles, members, phase, points, balance, sponsoredFactions);
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

            jsonObject.addProperty("phase", revolution.getPhase().toString());
            jsonObject.addProperty("points", revolution.getPoints());

            jsonObject.add("sponsoredFactions", context.serialize(revolution.getSponsoringFactions()));

            return jsonObject;
        }
        return null;
    }
}
