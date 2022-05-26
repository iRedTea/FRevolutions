package me.redtea.factionrevolutions.db.impl.json.adapter;

import com.google.gson.*;
import me.redtea.factionrevolutions.types.impl.*;

import java.lang.reflect.*;
import java.util.UUID;

public class RPlayerSerializer implements JsonSerializer<RPlayer>, JsonDeserializer<RPlayer> {

    @Override
    public RPlayer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        if(jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String uuid = null, revolution = null;
            boolean inRevolution = false;

            if(jsonObject.has("uuid")) {
                uuid = jsonObject.get("uuid").getAsString();
            }

            if(jsonObject.has("revolution")) {
                revolution = jsonObject.get("revolution").getAsString();
            }

            if(jsonObject.has("inRevolution")) {
                inRevolution = jsonObject.get("inRevolution").getAsBoolean();
            }

            return new RPlayer(UUID.fromString(uuid), inRevolution, revolution);
        }
        return null;
    }

    @Override
    public JsonElement serialize(RPlayer player, Type type, JsonSerializationContext context) {
        if(type != null) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("uuid", player.getUuid().toString());
            jsonObject.addProperty("revolution", player.getRevolution());
            jsonObject.addProperty("inRevolution", player.isInRevolution());
            
            return jsonObject;
        }
        return null;
    }
}
