package kr.pe.karsei.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;

public class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            /*
            messageCode = required.item
            messageCode = required
             */
            System.out.println("messageCode = " + messageCode);
        }
        Assertions.assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            /*
            messageCode = required.item.itemName
            messageCode = required.itemName
            messageCode = required.java.lang.String
            messageCode = required
             */
            System.out.println("messageCode = " + messageCode);
        }
        //new FieldError("item", "itemName", null, false, messageCodes, null, null);
        Assertions.assertThat(messageCodes).containsExactly(
                "required.item.itemName"
                , "required.itemName"
                , "required.java.lang.String"
                , "required"
        );
    }
}
