package com.example.ticket_machine.tools;

import com.example.ticket_machine.models.Event;
import com.example.ticket_machine.models.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JsonParser class allows parsing JSONObjects retrieved from API to specified objects of this application.
 */

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

    public static Event getEvent(JSONObject object) throws JSONException {
        Event result = new Event();

        result.Id = object.getString("id").trim();
        result.Name = object.getString("name").trim();
        result.Description = object.getString("description").trim();
        result.Price = object.getString("price").trim();

        return result;
    }
}
