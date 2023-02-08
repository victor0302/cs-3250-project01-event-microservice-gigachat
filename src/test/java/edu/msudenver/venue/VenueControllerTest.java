package edu.msudenver.venue;

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
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = VenueController.class)
public class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VenueRepository venueRepository;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private EntityManager entityManager;

    @SpyBean
    private VenueService venueService;

    public VenueControllerTest() {
    }

    @BeforeEach
    public void setup() {
        venueService.entityManager = entityManager;
    }


    @Test
    public void testGetVenues() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/venues/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Venue testVenue = new Venue();
        testVenue.setVenueId(1L);
        testVenue.setName("Somewhere");
        testVenue.setStreetAddress("1234 Someplace Ln");
        testVenue.setType("public");
        testVenue.setActive(true);

        Mockito.when(venueRepository.findAll()).thenReturn(Arrays.asList(testVenue));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Somewhere"));
    }

    @Test
    public void testGetVenue() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/venues/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Venue testVenue = new Venue();
        testVenue.setVenueId(1L);
        testVenue.setName("Somewhere");
        testVenue.setStreetAddress("1234 Someplace Ln");
        testVenue.setType("public");
        testVenue.setActive(true);


        Mockito.when(venueRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testVenue));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("1"));
    }

    @Test
    public void testGetVenueNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/venues/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(venueRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testCreateVenue() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/venues/")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"venueId\":\"1\", \"name\": \"Somewhere\", \"streetAddress\":\"\"1234 Someplace Ln\"\", \"Type\": \"\"public\"\", \"Active\":\"true\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Venue testVenue = new Venue();
        testVenue.setVenueId(1L);
        testVenue.setName("Somewhere");
        testVenue.setStreetAddress("1234 Someplace Ln");
        testVenue.setType("public");
        testVenue.setActive(true);

        Mockito.when(venueRepository.saveAndFlush(Mockito.any())).thenReturn(testVenue);
        Mockito.when(venueRepository.save(Mockito.any())).thenReturn(testVenue);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();


    }

    @Test
    public void testCreateVenueBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/venues/")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"venueId\":\"1\", \"name\": \"Somewhere\", \"streetAddress\":\"\"1234 Someplace Ln\"\", \"Type\": \"\"public\"\", \"Active\":\"true\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(venueRepository.save(Mockito.any())).thenThrow(IllegalArgumentException.class);
        Mockito.when(venueRepository.saveAndFlush(Mockito.any())).thenThrow(IllegalArgumentException.class);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testUpdateVenue() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/venues/1")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"venueId\":\"1\", \"name\": \"Somewhere\", \"streetAddress\":\"\"1234 Someplace Ln\"\", \"Type\": \"\"public\"\", \"Active\":\"true\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Venue testVenue = new Venue();
        testVenue.setVenueId(1L);
        testVenue.setName("Somewhere");
        testVenue.setStreetAddress("1234 Someplace Ln");
        testVenue.setType("public");
        testVenue.setActive(true);

        Mockito.when(venueRepository.findById(Mockito.any())).thenReturn(Optional.of(testVenue));

        Venue testVenueUpdated = new Venue();
        testVenue.setVenueId(1L);
        testVenue.setName("Somewhere New!");
        testVenue.setStreetAddress("1234 Someplace Ln");
        testVenue.setType("public");
        testVenue.setActive(true);

        Mockito.when(venueRepository.save(Mockito.any())).thenReturn(testVenueUpdated);
        Mockito.when(venueRepository.saveAndFlush(Mockito.any())).thenReturn(testVenueUpdated);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();


    }

    @Test
    public void testUpdateVenueNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/venues/1")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"venueId\":\"1\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(venueRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testUpdateVenueBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/venues/1")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"venueId\":\"5\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Venue testVenue = new Venue();
        testVenue.setVenueId(1L);
        testVenue.setName("Somewhere");
        testVenue.setStreetAddress("1234 Someplace Ln");
        testVenue.setType("public");
        testVenue.setActive(true);

        Mockito.when(venueRepository.findById(Mockito.any())).thenReturn(Optional.of(testVenue));

        Mockito.when(venueRepository.save(Mockito.any())).thenThrow(IllegalArgumentException.class);
        Mockito.when(venueRepository.saveAndFlush(Mockito.any())).thenThrow(IllegalArgumentException.class);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeleteVenue() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/venues/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Venue testVenue = new Venue();
        testVenue.setVenueId(1L);
        testVenue.setName("Somewhere");
        testVenue.setStreetAddress("1234 Someplace Ln");
        testVenue.setType("public");
        testVenue.setActive(true);

        Mockito.when(venueRepository.findById(Mockito.any())).thenReturn(Optional.of(testVenue));
        Mockito.when(venueRepository.existsById(Mockito.any())).thenReturn(true);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void testDeleteVenueNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/venues/5")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(venueRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(venueRepository.existsById(Mockito.any())).thenReturn(false);
        Mockito.doThrow(IllegalArgumentException.class)
                .when(venueRepository)
                .deleteById(Mockito.any());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }
}