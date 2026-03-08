package cat.itacademy.s04.t02.n01.fruit_Api.controller;


import cat.itacademy.s04.t02.n01.fruit_Api.exceptions.FruitNotFoundException;
import cat.itacademy.s04.t02.n01.fruit_Api.model.FruitDto;
import cat.itacademy.s04.t02.n01.fruit_Api.service.FruitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FruitController.class)
public class FruitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FruitService fruitService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturn201_WhenValid() throws Exception {
        FruitDto inputDto = new FruitDto(null, "Apple", 0.5);
        FruitDto outputDto = new FruitDto(1L, "Apple", 0.5);

        when(fruitService.createFruit(any(FruitDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    void create_ShouldReturn400_WhenInvalidData() throws Exception {
        FruitDto invalidDto = new FruitDto(null, "", -1.0);

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(fruitService, never()).createFruit(any());
    }

    @Test
    void getAll_ShouldReturnList() throws Exception {
        when(fruitService.getAll()).thenReturn(List.of(new FruitDto(1L, "Banana", 0.2)));

        mockMvc.perform(get("/fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Banana"));
    }

    @Test
    void getById_ShouldReturn200_WhenExists() throws Exception {
        FruitDto dto = new FruitDto(1L, "Orange", 0.3);
        when(fruitService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/fruits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Orange"));
    }

    @Test
    void getById_ShouldReturn404_WhenNotFound() throws Exception {
        when(fruitService.getById(99L)).thenThrow(new FruitNotFoundException("Not found"));

        mockMvc.perform(get("/fruits/99"))
                .andExpect(status().isNotFound()); // Verifica tu GlobalExceptionHandler
    }

    @Test
    void update_ShouldReturn200_WhenValid() throws Exception {
        FruitDto updateDto = new FruitDto(null, "Mango", 0.8);
        FruitDto resultDto = new FruitDto(1L, "Mango", 0.8);

        when(fruitService.update(eq(1L), any(FruitDto.class))).thenReturn(resultDto);

        mockMvc.perform(put("/fruits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mango"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(fruitService).delete(1L);

        mockMvc.perform(delete("/fruits/1"))
                .andExpect(status().isNoContent());

        verify(fruitService, times(1)).delete(1L);
    }
}
