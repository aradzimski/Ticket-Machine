package com.example.ticket_machine.tools;

import com.example.ticket_machine.models.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

public final class JsonParser {

    public static Ticket getTicket(JSONObject object) throws JSONException {
        Ticket result = new Ticket();

        result.Id = object.getString("id").trim();
        result.EventId = object.getString("event_id").trim();
        result.UserId = object.getString("user_id").trim();
        result.Key = object.getString("key").trim();
        result.CreatedOn = object.getString("createdOn").trim();
        result.EventName = object.getString("name").trim();

        return result;
    }
}
