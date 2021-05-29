theme: /
    
    state: recip
        a: Товар есть в наличии от нескольких производителей.
        a: Обратите внимание: доставка данного товара невозможна, только самовывоз из аптеки номер 1.
        a: Показать сначала  товары дешевле или сначала более популярные?
        buttons:
            "Сначала дешевле"
            "По популярности"
        
        state: 1
            event: noMatch
            go!: /recip_choose
             

    state: recip_choose
        a: В наличии имеется {{$client.httpResponse[0].name}}, ID: {{$client.httpResponse[0].productid}}, действующее вещество - {{$client.httpResponse[0].mnnname}}, цена - {{$client.httpResponse[0].price}}.  Оформить товар или показать следующий? 
        image: {{$client.httpResponse[0].image}}
        state: 1
            event: noMatch
            go!: /recip_choose_next
        state: 2
            q: * (*оформить*|*купить*|*оформ*) *
            go!: /recip_order
        state: 3
            q: * (*следующ*|*далее*|*еще*) * 
            go!: /recip_choose_next        
        script: 
             $reactions.buttons({ text: 'Оформить', transition: '/recip_order' }); 
             $reactions.buttons({ text: 'Следующий', transition: '/recip_choose_next' });

    state: recip_choose_next
        a: Есть {{$client.httpResponse[1].name}}, ID: {{$client.httpResponse[1].productid}}, действующее вещество - {{$client.httpResponse[1].mnnname}} цена - {{$client.httpResponse[1].price}}.  Оформить товар или показать следующий? 
        image: {{$client.httpResponse[1].image}}
        state: 1
            event: noMatch
            go!: /recip_choose
        state: 2
            q: * (*оформить*|*купить*|*оформ*) *
            go!: /recip_order
        state: 3
            q: * (*следующ*|*далее*|*еще*) * 
            go!: /recip_choose        
        script: 
             $reactions.buttons({ text: 'Оформить', transition: '/recip_order' }); 
             $reactions.buttons({ text: 'Следующий', transition: '/recip_choose' });  
             
    state: recip_order
        a: Ближайший аптечный пункт - Аптека №1. Ближайший срок доставки - сегодня, после 20-00. Подтверждаете ваш заказ? 
        state: 1
            q: * (*да*|*желаю*|*ок*|*хочу*) *
            go!: /recip_order_ok
        state: 2 
            event: noMatch
            go!: ../../CatchAll
        script: 
             $reactions.buttons({ text: 'Подтверждаю', transition: '/recip_order_ok' }); 
             $reactions.buttons({ text: 'Нет', transition: '../first' });  
             
    state: recip_order_ok
        a: Успешно! Я обратил внимание, что вы заказываете этот товар примерно раз в месяц. Желаете этот товар добавить в автодоставку с периодичностью раз вмесяц?  
        state: 1
            event: noMatch
            go!: /recip_order_reminder
        state: 2
            q: * (*да*|*желаю*|*ок*|*хочу*) *
            q: * (*подписка*|*подпис*|*доставк*|*доставку*) *
            go!: /recip_order_delivery
        script: 
             $reactions.buttons({ text: 'Подписка на автодоставку', transition: '/recip_order_delivery' }); 
             $reactions.buttons({ text: 'Нет', transition: '/recip_order_reminder' });        

    state: recip_order_delivery        
        a: Подписка раз в месяц на {{$client.httpResponse[1].name}} успешно активирована! Данное лекарство требует регулярного приема {{$client.httpResponse[1].doze}}. Желаете установить напоминание о приеме?
        state: 1
            event: noMatch
            go!: /refuse
        state: 2
            q: * (*да*|*желаю*|*ок*|*хочу*) *
            go!: ../../Reminder
        script: 
             $reactions.buttons({ text: 'Хочу напоминания', transition: '../Reminder' }); 
             $reactions.buttons({ text: 'Нет', transition: '../first' });         
        
    state: recip_order_reminder        
        a: Данное лекарство требует регулярного приема {{$client.httpResponse[1].doze}}. Желаете установить напоминание о приеме?
        state: 1
            event: noMatch
            go!: /refuse
        state: 2
            q: * (*да*|*желаю*|*ок*|*хочу*) *
            go!: ../../Reminder
        script: 
             $reactions.buttons({ text: 'Хочу напоминания', transition: '../Reminder' }); 
             $reactions.buttons({ text: 'Нет', transition: '../first' });   
             
    state: refuse
        a: Лекарство будет гораздо эффективнее, если его правильно хранить: например именно это нужно хранить в при комнатной температуре, но ни в коем случае не в холодильнике. Будьте здоровы! Могу вам еще чем-нибудь помочь? 
        state: 1
            event: noMatch
            go!: ../../first        
        script: 
             $reactions.buttons({ text: 'Выбрать товар', transition: '/chooseItem' }); 
             $reactions.buttons({ text: 'Подобрать аналог', transition: '../analogue' }); 
             $reactions.buttons({ text: 'Поставить напоминание', transition: '../Reminder' });

        