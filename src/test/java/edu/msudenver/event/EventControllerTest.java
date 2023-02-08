package edu.msudenver.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private EntityManager entityManager;

    @SpyBean
    private EventService eventService;

    public EventControllerTest() throws ParseException {
    }

    @BeforeEach
    public void setup() {
        eventService.entityManager = entityManager;
    }

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    String dateInString = "25-JAN-2023";
    Date date = formatter.parse(dateInString);
    String dateInString2 = "26-JAN-2023";
    Date date2 = formatter.parse(dateInString2);

    @Test
    public void testGetEvents() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/events/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Event testEvent = new Event();
        testEvent.setEventId(1L);
        testEvent.setTitle("Christmas");
        testEvent.setStarts(date);
        testEvent.setEnds(date2);
        testEvent.setVenueId(1L);

        Mockito.when(eventRepository.findAll()).thenReturn(Arrays.asList(testEvent));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Christmas"));
    }

    @Test
    public void testGetEvent() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/events/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Event testEvent = new Event();
        testEvent.setEventId(1L);
        testEvent.setTitle("Christmas");
        testEvent.setStarts(date);
        testEvent.setEnds(date2);
        testEvent.setVenueId(1L);

        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testEvent));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("1"));
    }

    @Test
    public void testGetEventNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/events/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testCreateEvent() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/events/")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"eventId\":\"1\", \"title\": \"Christmas\", \"starts\":\"\"25-JAN-2023\"\", \"ends\": \"\"26-JAN-2023\"\", \"venueId\":\"1\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Event testEvent = new Event();
        testEvent.setEventId(1L);
        testEvent.setTitle("Christmas");
        testEvent.setStarts(date);
        testEvent.setEnds(date2);
        testEvent.setVenueId(1L);
        Mockito.when(eventRepository.saveAndFlush(Mockito.any())).thenReturn(testEvent);
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(testEvent);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();



    }

    @Test
    public void testCreateEventBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/events/")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"eventId\":\"1\", \"title\": \"Christmas\", \"starts\":\"\"25-DEC-2022\"\", \"ends\": \"\"26-DEC-2022\"\", \"venueId\":\"1\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(eventRepository.save(Mockito.any())).thenThrow(IllegalArgumentException.class);
        Mockito.when(eventRepository.saveAndFlush(Mockito.any())).thenThrow(IllegalArgumentException.class);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/events/1")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"eventId\":\"1\", \"title\": \"Christmas Updated\", \"starts\":\"\"25-DEC-2022\"\", \"ends\": \"\"26-DEC-2022\"\", \"venueId\":\"1\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Event testEvent = new Event();
        testEvent.setEventId(1L);
        testEvent.setTitle("Christmas");
        testEvent.setStarts(date);
        testEvent.setEnds(date2);
        testEvent.setVenueId(1L);
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(testEvent));

        Event testEventUpdated = new Event();
        testEvent.setEventId(1L);
        testEvent.setTitle("Christmas Updated");
        testEvent.setStarts(date);
        testEvent.setEnds(date2);
        testEvent.setVenueId(1L);
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(testEventUpdated);
        Mockito.when(eventRepository.saveAndFlush(Mockito.any())).thenReturn(testEventUpdated);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();



    }

    @Test
    public void testUpdateEventNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/events/1")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"eventId\":\"1\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testUpdateEventBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/events/1")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"eventId\":\"5\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Event testEvent = new Event();
        testEvent.setEventId(1L);
        testEvent.setTitle("Christmas");
        testEvent.setStarts(date);
        testEvent.setEnds(date2);
        testEvent.setVenueId(1L);
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(testEvent));

        Mockito.when(eventRepository.save(Mockito.any())).thenThrow(IllegalArgumentException.class);
        Mockito.when(eventRepository.saveAndFlush(Mockito.any())).thenThrow(IllegalArgumentException.class);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeleteEvent() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/events/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Event testEvent = new Event();
        testEvent.setEventId(1L);
        testEvent.setTitle("Christmas");
        testEvent.setStarts(date);
        testEvent.setEnds(date2);
        testEvent.setVenueId(1L);
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(testEvent));
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(true);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void testDeleteEventNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/events/5")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(false);
        Mockito.doThrow(IllegalArgumentException.class)
                .when(eventRepository)
                .deleteById(Mockito.any());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }
}
