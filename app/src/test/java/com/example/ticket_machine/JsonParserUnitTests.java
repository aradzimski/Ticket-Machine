package com.example.ticket_machine;

import com.example.ticket_machine.models.Event;
import com.example.ticket_machine.models.Ticket;
import com.example.ticket_machine.tools.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class JsonParserUnitTests {

    @Test
    public void jsonParserGetTicketMethodReturnsTicketObject() throws JSONException {
        JsonParser jsonParser = new JsonParser();

        JSONObject ticketJSON = new JSONObject();
        ticketJSON.put("id", "1");
        ticketJSON.put("event_id", "1");
        ticketJSON.put("user_id", "1");
        ticketJSON.put("key", "198f58587446470aac972f970d332025");
        ticketJSON.put("createdOn", "2020-06-07 14:00:00");
        ticketJSON.put("name", "Test Event");

        assertThat(jsonParser.getTicket(ticketJSON), instanceOf(Ticket.class));
    }

    @Test
    public void jsonParserGetEventMethodReturnsEventObject() throws JSONException {
        JsonParser jsonParser = new JsonParser();

        JSONObject eventJSON = new JSONObject();
        eventJSON.put("id", "1");
        eventJSON.put("name", "Test Event");
        eventJSON.put("description", "Test description");
        eventJSON.put("price", "20.00");

        assertThat(jsonParser.getEvent(eventJSON), instanceOf(Event.class));
    }

    @Test
    public void jsonParserReturnTicketWithCorrectID() throws JSONException {
        JsonParser jsonParser = new JsonParser();

        JSONObject ticketJSON = new JSONObject();
        ticketJSON.put("id", "1");
        ticketJSON.put("event_id", "1");
        ticketJSON.put("user_id", "1");
        ticketJSON.put("key", "198f58587446470aac972f970d332025");
        ticketJSON.put("createdOn", "2020-06-07 14:00:00");
        ticketJSON.put("name", "Test Event");

        Ticket ticket = jsonParser.getTicket(ticketJSON);

        assertEquals("1", ticket.Id);
    }

    @Test
    public void jsonParserReturnEventWithCorrectID() throws JSONException {
        JsonParser jsonParser = new JsonParser();

        JSONObject eventJSON = new JSONObject();
        eventJSON.put("id", "1");
        eventJSON.put("name", "Test Event");
        eventJSON.put("description", "Test description");
        eventJSON.put("price", "20.00");

        Event event = jsonParser.getEvent(eventJSON);

        assertEquals("1", event.Id);
    }
}