package kr.pe.karsei.itemservice.web.item;

import kr.pe.karsei.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        // item == clazz
        // item == subItem (자식 클래스)
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required"); // "required.item.itemName"
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName", "required");

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null); // "range.item.price"
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null); // "max.item.quantity"
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int calPrice = item.getPrice() * item.getQuantity();
            if (calPrice < 10000) {
                errors.reject("totalPriceMin");
            }
        }
    }
}
