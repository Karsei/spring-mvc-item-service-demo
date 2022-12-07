package kr.pe.karsei.itemservice.web.item;

import kr.pe.karsei.itemservice.domain.item.DeliveryCode;
import kr.pe.karsei.itemservice.domain.item.Item;
import kr.pe.karsei.itemservice.domain.item.ItemRepository;
import kr.pe.karsei.itemservice.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class BasicItemControllerV2 {
    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId,
                       Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item,
                            BindingResult bindingResult, // @ModalAttribute 바로 다음에 와야함 
                            RedirectAttributes redirectAttributes) {
        // 검증
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000원까지만 가능합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999개까지 가능합니다."));
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int calPrice = item.getPrice() * item.getQuantity();
            if (calPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. (현재 값 = " + calPrice + ")"));
            }
        }
        // 실패 시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}"; // ?status=true
    }

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item,
                            BindingResult bindingResult, // @ModalAttribute 바로 다음에 와야함
                            RedirectAttributes redirectAttributes) {
        log.info("objectName = {}", bindingResult.getObjectName()); // objectName=item //@ModelAttribute name
        log.info("target = {}", bindingResult.getTarget()); // target=Item(id=null, itemName=상품, price=100, quantity=1234)

        // 검증
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다.", false, new String[]{"required.item.itemName"}, null, "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000원까지만 가능합니다.", false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, "가격은 {0} ~ {1} 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999개까지 가능합니다.", false, new String[]{"max.item.quantity"}, new Object[]{9999}, "수량은 최대 {0} 까지 허용합니다."));
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int calPrice = item.getPrice() * item.getQuantity();
            if (calPrice < 10000) {
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, calPrice}, "가격 * 수량의 합은 10,000원 이상이어야 합니다. (현재 값 = " + calPrice + ")"));
            }
        }
        // 실패 시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}"; // ?status=true
    }

    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item,
                            BindingResult bindingResult, // @ModalAttribute 바로 다음에 와야함
                            RedirectAttributes redirectAttributes) {
        log.info("objectName = {}", bindingResult.getObjectName()); // objectName=item //@ModelAttribute name
        log.info("target = {}", bindingResult.getTarget()); // target=Item(id=null, itemName=상품, price=100, quantity=1234)

        // 검증
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required"); // "required.item.itemName"
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null); // "range.item.price"
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null); // "max.item.quantity"
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int calPrice = item.getPrice() * item.getQuantity();
            if (calPrice < 10000) {
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, calPrice}, "가격 * 수량의 합은 10,000원 이상이어야 합니다. (현재 값 = " + calPrice + ")"));
                bindingResult.reject("totalPriceMin");
            }
        }
        // 실패 시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}"; // ?status=true
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId,
                           Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId,
                       @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @ModelAttribute("regions")
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");
        return regions;
    }

    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        return ItemType.values();
    }

    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }

    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 1000, 2));
        itemRepository.save(new Item("itemB", 2000, 1));
    }
}
