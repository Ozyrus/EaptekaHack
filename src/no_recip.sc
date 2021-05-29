theme: /

    state: no_recip
        a: Товар есть в наличии от нескольких производителей.
        a: Обратите внимание: доставка данного товара невозможна, только самовывоз из аптеки номер 1.
        a: Показать сначала  товары дешевле или сначала более популярные?
        buttons:
            "Сначала дешевле"
            "По популярности"
        
        state: 1
            q: Сначала дешевле
            go!: /noRecipChoose
            
        state: 2
            q: По популярности
            go!: /noRecipChoose
            
        state: 3
            event: noMatch
            go!: /noRecipChoose

    state: noRecipChoose
        a: В наличии имеется {{$client.httpResponse[5].name}}, ID: {{$client.httpResponse[5].productid}}, действующее вещество - {{$client.httpResponse[5].mnnname}}, цена - {{$client.httpResponse[5].price}}.  Оформить товар или показать следующий? 
        image: {{$client.httpResponse[5].image}}
        script: 
             $reactions.buttons({ text: "Оформить", transition: "/no_recip_order" }); 
             $reactions.buttons({ text: "Следующий", transition: "/no_recip_choose_next" });
             
        state: 1
            event: noMatch
            go!: /no_recip_choose_next
        state: 2
            q: * (*оформить*|*купить*|*оформ*) *
            go!: /no_recip_order
        state: 3
            q: * (*следующ*|*далее*|*еще*) * 
            go!: /no_recip_choose_next        

    state: no_recip_choose_next
        a: Есть {{$client.httpResponse[6].name}}, ID: {{$client.httpResponse[6].productid}}, действующее вещество - {{$client.httpResponse[0].mnnname}} цена - {{$client.httpResponse[6].price}}.  Оформить товар или показать следующий? 
        image: {{$client.httpResponse[6].image}}
        script: 
             $reactions.buttons({ text: "Оформить", transition: "/no_recip_order" }); 
             $reactions.buttons({ text: "Следующий", transition: "/no_recip_choose" }); 
             
        state: 1
            event: noMatch
            go!: /no_recip_choose
        state: 2
            q: * (*оформить*|*купить*|*оформ*) *
            go!: /no_recip_order
        state: 3
            q: * (*следующ*|*далее*|*еще*) * 
            go!: /no_recip_choose        
             
    state: no_recip_order
        a: Ближайший аптечный пункт - Аптека №1. Ближайший срок доставки - сегодня, после 20-00. Сумма покупки - {{$client.httpResponse[6].price}}. Подтверждаете ваш заказ? 
        script: 
             $reactions.buttons({ text: "Подтверждаю", transition: "/no_recip_order_ok" }); 
             $reactions.buttons({ text: "Нет", transition: "/refuse1" });  
             
        state: 1
            event: noMatch
            go!: /no_recip_order_ok
            
    state: no_recip_order_ok
        a: Успешно! Я обратил внимание, что вы заказываете этот товар примерно раз в месяц. Желаете этот товар добавить в автодоставку с периодичностью раз в месяц?  
        script: 
             $reactions.buttons({ text: "Подписка на автодоставку", transition: "/no_recip_order_delivery" }); 
             $reactions.buttons({ text: "Нет", transition: "/no_recip_order_reminder" }); 
             
        state: 1
            event: noMatch
            go!: /no_recip_order_reminder
        state: 2
            q: * (*да*|*желаю*|*ок*|*хочу*) *
            q: * (*подписка*|*подпис*|*доставк*|*доставку*) *
            go!: /no_recip_order_delivery
        
    state: no_recip_order_reminder
        a: Данное лекарство требует регулярного приема {{$client.httpResponse[6].doze}}. Желаете установить напоминание о приеме?
        script: 
             $reactions.buttons({ text: "Хочу напоминания", transition: "../Reminder" }); 
             $reactions.buttons({ text: "Нет", transition: "../first" }); 
             
        state: 1
            event: noMatch
            go!: /refuse1
        state: 2
            q: * (*да*|*желаю*|*ок*|*хочу*) *
            go!: ../../Reminder
             
    state: no_recip_order_delivery        
        a: Подписка на лекарство раз в месяц активирована. Данное лекарство требует регулярного приема {{$client.httpResponse[6].doze}}. Желаете установить напоминание о приеме?
        script: 
             $reactions.buttons({ text: "Хочу напоминания", transition: "../Reminder" }); 
             $reactions.buttons({ text: "Нет", transition: "../first" }); 
             
        state: 1
            event: noMatch
            go!: /refuse1
        state: 2
            q: * (*да*|*желаю*|*ок*|*хочу*) *
            go!: ../../Reminder
             
    state: refuse1
        a: Могу вам еще чем-нибудь помочь? 
        script: 
             $reactions.buttons({ text: "Выбрать товар", transition: "/chooseItem" }); 
             $reactions.buttons({ text: "Подобрать аналог", transition: "../analogue" }); 
             $reactions.buttons({ text: "Поставить напоминание", transition: "../Reminder" });
             
        state: 1
            event: noMatch
            go!: ../../first