package cat.itacademy.s04.t02.n01.fruit_Api.service;

import cat.itacademy.s04.t02.n01.fruit_Api.exceptions.FruitAlreadyExistsException;
import cat.itacademy.s04.t02.n01.fruit_Api.exceptions.FruitNotFoundException;
import cat.itacademy.s04.t02.n01.fruit_Api.model.Fruit;
import cat.itacademy.s04.t02.n01.fruit_Api.model.FruitDto;
import cat.itacademy.s04.t02.n01.fruit_Api.repository.FruitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FruitService {

    private final FruitRepository fruitRepository;
    public FruitService(FruitRepository fruitRepository) {
        this.fruitRepository = fruitRepository;
    }

    @Transactional
    public FruitDto createFruit(FruitDto fruitDto){
        if(fruitRepository.existsByName(fruitDto.name())){
            throw new FruitAlreadyExistsException("This fruit" + fruitDto.name() + "already exists");
        }

        Fruit savedFruit = fruitRepository.save(FruitMapper.toEntity(fruitDto));

        return FruitMapper.toDto(savedFruit);
    }

    public List<FruitDto> getAll(){
        return fruitRepository.findAll().stream()
                .map(FruitMapper::toDto)
                .toList();
    }

    public FruitDto getById(Long id){
        return fruitRepository.findById(id)
                .map(FruitMapper::toDto)
                .orElseThrow(() -> new FruitNotFoundException("Not found ID: " + id));
    }

    @Transactional
    public FruitDto update(Long id, FruitDto fruitDto){
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new FruitNotFoundException("Cannot update, ID not found: " + id));

        fruit.setName(fruitDto.name());
        fruit.setWeightKg(fruitDto.weightKg());

        return FruitMapper.toDto(fruitRepository.save(fruit));
    }

    @Transactional
    public void delete(Long id) {
        if (!fruitRepository.existsById(id)) {
            throw new FruitNotFoundException("Cannot delete, ID not found: " + id);
        }
        fruitRepository.deleteById(id);
    }
}
