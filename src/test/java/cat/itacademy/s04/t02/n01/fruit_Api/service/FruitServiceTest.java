package cat.itacademy.s04.t02.n01.fruit_Api.service;


import cat.itacademy.s04.t02.n01.fruit_Api.exceptions.FruitAlreadyExistsException;
import cat.itacademy.s04.t02.n01.fruit_Api.exceptions.FruitNotFoundException;
import cat.itacademy.s04.t02.n01.fruit_Api.model.Fruit;
import cat.itacademy.s04.t02.n01.fruit_Api.model.FruitDto;
import cat.itacademy.s04.t02.n01.fruit_Api.repository.FruitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FruitServiceTest {

    @Mock
    private FruitRepository fruitRepository;

    @InjectMocks
    private FruitService fruitService;

    @Test
    void getById_ShouldReturnFruit_WhenIdExists() {
        FruitDto fruitDto = new FruitDto(null, "Apple", 0.2);
        Fruit fruit = Fruit.builder().id(10L).name("Apple").weightKg(0.2).build();

        when(fruitRepository.existsByName("Apple")).thenReturn(false);
        when(fruitRepository.save(any(Fruit.class))).thenReturn(fruit);

        FruitDto result = fruitService.createFruit(fruitDto);

        assertNotNull(result.id());
        assertEquals("Apple", result.name());
        verify(fruitRepository).save(any(Fruit.class));
    }

    @Test
    void createFruit_ShouldThrowException_WhenNameAlreadyExists() {
        FruitDto duplicate = new FruitDto(null, "Apple", 0.5);
        when(fruitRepository.existsByName("Apple")).thenReturn(true);

        assertThrows(FruitAlreadyExistsException.class, () -> fruitService.createFruit(duplicate));
        verify(fruitRepository, never()).save(any());
    }

    @Test
    void getAll_ShouldReturnListOfDtos() {
        List<Fruit> entities = List.of(
                Fruit.builder().id(1L).name("Apple").build(),
                Fruit.builder().id(2L).name("Orange").build()
        );
        when(fruitRepository.findAll()).thenReturn(entities);

        List<FruitDto> result = fruitService.getAll();

        assertEquals(2, result.size());
        assertEquals("Apple", result.get(0).name());
        assertEquals("Orange", result.get(1).name());
    }

    @Test
    void update_ShouldModifyAndSave_WhenIdExists() {
        Long id = 1L;
        Fruit existingFruit = Fruit.builder().id(id).name("Old Name").weightKg(1.0).build();
        FruitDto updateInfo = new FruitDto(null, "New Name", 1.5);

        when(fruitRepository.findById(id)).thenReturn(Optional.of(existingFruit));
        when(fruitRepository.save(any(Fruit.class))).thenAnswer(i -> i.getArguments()[0]);

        FruitDto result = fruitService.update(id, updateInfo);

        assertEquals("New Name", result.name());
        assertEquals(1.5, result.weightKg());
        verify(fruitRepository).save(any(Fruit.class));
    }

    @Test
    void delete_ShouldCallRepository_WhenIdExists() {
        when(fruitRepository.existsById(1L)).thenReturn(true);
        doNothing().when(fruitRepository).deleteById(1L);

        fruitService.delete(1L);

        verify(fruitRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowNotFound_WhenIdDoesNotExist() {
        when(fruitRepository.existsById(99L)).thenReturn(false);

        assertThrows(FruitNotFoundException.class, () -> fruitService.delete(99L));
        verify(fruitRepository, never()).deleteById(anyLong());
    }


}
