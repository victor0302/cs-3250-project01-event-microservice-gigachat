package edu.msudenver.city;

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
@WebMvcTest(value = CityController.class)
public class CityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityRepository cityRepository;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private EntityManager entityManager;

    @SpyBean
    private CityService cityService;

    @BeforeEach
    public void setup() {
        cityService.entityManager = entityManager;
    }

    @Test
    public void testGetCities() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/cities/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        City testCity = new City();
        testCity.setPostalCode("80208");
        testCity.setCountryCode("United States");
        testCity.setCountryCode("us");
        testCity.setName("Denver");

        Mockito.when(cityRepository.saveAndFlush(Mockito.any())).thenReturn(Arrays.asList(testCity));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains(""));
    }

    @Test
    public void testGetCity() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/cities/us/80208")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        City testCity = new City();
        testCity.setCountryCode("us");
        testCity.setPostalCode("80208");

        Mockito.when(cityRepository.findById(Mockito.any())).thenReturn(Optional.of(testCity));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("80208"));
    }

    @Test
    public void testGetCityNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/cities/notfound")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(cityRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testCreateCity() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/cities/")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"postalCode\":\"88888\",\"countryCode\":\"us\",\"cityName\":\"Chicago\"}")
                .contentType(MediaType.APPLICATION_JSON);

        City chicago = new City();
        chicago.setPostalCode("88888");
        chicago.setCountryCode("us");
        chicago.setName("Chicago");

        Mockito.when(cityRepository.saveAndFlush(Mockito.any())).thenReturn(chicago);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Chicago"));
    }

    @Test
    public void testCreateCityBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/cities")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"cityName\":\"Chicago\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(cityRepository.saveAndFlush(Mockito.any())).thenThrow(IllegalArgumentException.class);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testUpdateCity() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/cities/us/88888")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"postalCode\":\"88888\", \"countryCode\": \"us\", \"cityName\": \"Chicago Updated\"}")
                .contentType(MediaType.APPLICATION_JSON);

        City chicago = new City();
        chicago.setPostalCode("88888");
        chicago.setCountryCode("us");
        chicago.setName("Chicago");
        Mockito.when(cityRepository.findById(Mockito.any())).thenReturn(Optional.of(chicago));

        City chicagoUpdated = new City();
        chicago.setPostalCode("88888");
        chicagoUpdated.setCountryCode("us");
        chicagoUpdated.setName("Chicago Updated");
        Mockito.when(cityRepository.saveAndFlush(Mockito.any())).thenReturn(chicagoUpdated);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Chicago Updated"));
    }

    @Test
    public void testUpdateCityNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/cities/notfound")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"countryCode\":\"notfound\"}")
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(cityRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testUpdateCityBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/cities/us/88888")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"postalCode\":\"88888\", \"countryCode\": \"us\", \"cityName\": \"Chicago Updated\"}")
                .contentType(MediaType.APPLICATION_JSON);

        City chicago = new City();
        chicago.setPostalCode("88888");
        chicago.setCountryCode("us");
        chicago.setName("Chicago");
        Mockito.when(cityRepository.findById(Mockito.any())).thenReturn(Optional.of(chicago));

        Mockito.when(cityRepository.saveAndFlush(Mockito.any())).thenThrow(IllegalArgumentException.class);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeleteCity() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/cities/us/88888")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        City chicago = new City();
        chicago.setPostalCode("88888");
        chicago.setCountryCode("us");
        chicago.setName("Chicago");
        Mockito.when(cityRepository.findById(Mockito.any())).thenReturn(Optional.of(chicago));
        Mockito.when(cityRepository.existsById(Mockito.any())).thenReturn(true);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void testDeleteCityNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/cities/notfound")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        Mockito.when(cityRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(cityRepository.existsById(Mockito.any())).thenReturn(false);
        Mockito.doThrow(IllegalArgumentException.class)
                .when(cityRepository)
                .deleteById(Mockito.any());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }
}
