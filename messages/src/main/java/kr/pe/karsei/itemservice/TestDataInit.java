package kr.pe.karsei.itemservice;

import kr.pe.karsei.itemservice.domain.item.Item;
import kr.pe.karsei.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final ItemRepository itemRepository;

    @PostConstruct
    public void init() {
        itemRepository.save(new Item("sampleItemA", 10000, 10));
        itemRepository.save(new Item("sampleItemB", 10000, 20));
    }
}
