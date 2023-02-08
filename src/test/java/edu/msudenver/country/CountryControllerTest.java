package edu.msudenver.country;

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
@WebMvcTest(value = CountryController.class)
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryRepository countryRepository;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private EntityManager entityManager;

    @SpyBean
    private CountryService countryService;

    @BeforeEach
    public void setup() {
        countryService.entityManager = entityManager;
    }

    @Test
    public void testGetCountries() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/countries/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Country testCountry = new Country();
        testCountry.setCountryCode("us");
        testCountry.setCountryName("United States");

        Mockito.when(countryRepository.findAll()).thenReturn(Arrays.asList(testCountry));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("United States"));
    }

    @Test
    public void testGetCountry() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/countries/us")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Country testCountry = new Country();
        testCountry.setCountryCode("us");
        testCountry.setCountryName("United States");

        Mockito.when(countryRepository.findById(Mockito.anyString())).thenReturn(Optional.of(testCountry));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("United States"));
    }

    @Test
    public void testGetCountryNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/countries/notfound")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(countryRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testCreateCountry() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/countries/")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"countryCode\":\"ca\", \"countryName\": \"Canada\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Country canada = new Country();
        canada.setCountryName("Canada");
        canada.setCountryCode("ca");
        Mockito.when(countryRepository.saveAndFlush(Mockito.any())).thenReturn(canada);
        Mockito.when(countryRepository.save(Mockito.any())).thenReturn(canada);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Canada"));
    }

    @Test
    public void testCreateCountryBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/countries/")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"countryName\": \"Canada\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(countryRepository.save(Mockito.any())).thenThrow(IllegalArgumentException.class);
        Mockito.when(countryRepository.saveAndFlush(Mockito.any())).thenThrow(IllegalArgumentException.class);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testUpdateCountry() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/countries/ca")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"countryCode\":\"ca\", \"countryName\": \"Canada Updated\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Country canada = new Country();
        canada.setCountryName("Canada");
        canada.setCountryCode("ca");
        Mockito.when(countryRepository.findById(Mockito.any())).thenReturn(Optional.of(canada));

        Country canadaUpdated = new Country();
        canadaUpdated.setCountryName("Canada Updated");
        canadaUpdated.setCountryCode("ca");
        Mockito.when(countryRepository.save(Mockito.any())).thenReturn(canadaUpdated);
        Mockito.when(countryRepository.saveAndFlush(Mockito.any())).thenReturn(canadaUpdated);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Canada Updated"));
    }

    @Test
    public void testUpdateCountryNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/countries/notfound")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"countryCode\":\"notfound\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(countryRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testUpdateCountryBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/countries/ca")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"countryCode\":\"ca\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Country canada = new Country();
        canada.setCountryName("Canada");
        canada.setCountryCode("ca");
        Mockito.when(countryRepository.findById(Mockito.any())).thenReturn(Optional.of(canada));

        Mockito.when(countryRepository.save(Mockito.any())).thenThrow(IllegalArgumentException.class);
        Mockito.when(countryRepository.saveAndFlush(Mockito.any())).thenThrow(IllegalArgumentException.class);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeleteCountry() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/countries/ca")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Country canada = new Country();
        canada.setCountryName("Canada");
        canada.setCountryCode("ca");
        Mockito.when(countryRepository.findById(Mockito.any())).thenReturn(Optional.of(canada));
        Mockito.when(countryRepository.existsById(Mockito.any())).thenReturn(true);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void testDeleteCountryNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/countries/notfound")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(countryRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(countryRepository.existsById(Mockito.any())).thenReturn(false);
        Mockito.doThrow(IllegalArgumentException.class)
                .when(countryRepository)
                .deleteById(Mockito.any());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }
}
