theme: /

    state: analogue
        a: Аналог какого действующего вещества или бренда вам нужен? (например, аспирин, ацетилсалициловая кислота)
        state: 1
            event: noMatch
            go!: /analogue_start
        state: 2
            q: * (*ацетилсалициловая*|*аспирин*|*аспирин*) *            
            go!: /analogue_start
            
    state: analogue_start                
        a: Аналоги товара {{$session.httpResponse[5].name}} с действующим веществом {{$session.httpResponse[5].mnnname}}: Найдено 3 аналога. Первый: {{$session.httpResponse[6].name}}, цена - {{$session.httpResponse[6].price}} Добавить в заказ или показать следующий? 
        image: {{$session.httpResponse[6].image}}
        state: 1
            q: * (*следующ*|*далее*|*еще*) *
            go!: /next_analogue
        state: 2
            q: * (*оформить*|*купить*|*оформ*) *
            go!: /analogue_order
        script: 
             $reactions.buttons({ text: 'Следующий', transition: '/next_analogue' }); 
             $reactions.buttons({ text: 'Добавить в заказ', transition: '/analogue_order' });
         
    state: next_analogue  
        a:  {{$session.httpResponse[7].name}}, цена - {{$session.httpResponse[7].price}} Добавить в заказ или показать следующий? 
        image: {{$session.httpResponse[7].image}}
        state: 1
            q: * (*следующ*|*далее*|*еще*) *
            go!: /next_one_analogue
        state: 2
            q: * (*оформить*|*купить*|*оформ*) *
            go!: /analogue_order
        script: 
             $reactions.buttons({ text: 'Следующий', transition: '/next_one_analogue' }); 
             $reactions.buttons({ text: 'Добавить в заказ', transition: '/analogue_order' });
             
    state: next_one_analogue            
        a:  {{$session.httpResponse[8].name}}, цена - {{$session.httpResponse[8].price}} Добавить в заказ или показать следующий? 
        image: {{$session.httpResponse[8].image}}
        state: 1
            q: * (*следующ*|*далее*|*еще*) *
            go!: /next_analogue
        state: 2
            q: * (*оформить*|*купить*|*оформ*) *
            go!: /analogue_order
        script: 
             $reactions.buttons({ text: 'Следующий', transition: '/next_analogue' }); 
             $reactions.buttons({ text: 'Добавить в заказ', transition: '/analogue_order' });
             
    state: analogue_order
        a: Успешно! Данное лекарство требует регулярного приема {{$session.httpResponse[8].doze}}. Желаете установить напоминание?
        state: 1
            event: noMatch
            go!: /analogue_order_refuse
        state: 2
            q: * (*да*|*желаю*|*ок*|*хочу*) *
            go!: ../../Reminder
        state: 3
            q: * (*нет*|*неа*|*не*) *
            go!: /analogue_order_refuse            
        script: 
             $reactions.buttons({ text: 'Хочу напоминания', transition: '../Reminder' }); 
             $reactions.buttons({ text: 'Нет', transition: '/analogue_order_refuse' });   

    state: analogue_order_refuse
        a: Лекарство будет гораздо эффективнее, если его правильно хранить: например именно это нужно хранить в при комнатной температуре, но ни в коем случае не в холодильнике. Будьте здоровы! Могу вам еще чем-нибудь помочь? 
        state: 1
            event: noMatch
            go!: ../../first        
        script: 
             $reactions.buttons({ text: 'Выбрать товар', transition: '/chooseItem' }); 
             $reactions.buttons({ text: 'Подобрать аналог', transition: '../analogue' }); 
             $reactions.buttons({ text: 'Поставить напоминание', transition: '../Reminder' });