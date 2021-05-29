#reminder.sc - сценарий напоминаний
#recip.sc - сценарий товара с возможной доставкой без рецепта
#no_recip.sc - сценарий товара по рецепту и только с самовывозом из аптеки
#analogue.sc - сценарий подбора аналогов

require: reminder.sc
require: phone.sc
require: no_recip.sc
require: recip.sc
require: analogue.sc
require: functions.js

require: slotfilling/slotFilling.sc
  module = sys.zb-common

require: common.js
  module = sys.zb-common

require: dateTime/dateTime.sc
  module = sys.zb-common

require: dateTime/moment.min.js
    module = sys.zb-common
    

theme: /
    
    state: first
        q: $regex</start>
        script:
            var headers = {
            };
            var result = $http.query("https://tools.aimylogic.com/api/googlesheet2json?id=1srAw0fcdHT19GJV8KR2MbBjDv8Ryq5DZV23JmK6fyoQ", {
                method: "GET",
                headers: headers,
                query: $session,
                dataType: "json",
                timeout: 0 || 10000
            });
            var $httpResponse = result.data;
            $session.httpStatus = result.status;
            $client.httpResponse = $httpResponse;
            if (result.isOk && result.status >= 200 && result.status < 300) {
            
                $reactions.transition("/start");
            } else {
                $reactions.transition("/errorHttp");
            };
        go!: /start   
        
    state: start
        a: Я ваш смарт-ассистент умной аптеки. Чем могу помочь?
        state: 2
            q: * (*выбрать*|*лекарство*|*рецепт*|*подобрать*) *
            go!: /chooseItemNoRecip
        state: 3
            q: * (*аналог*|*аналогичное*|*аналог*) * 
            go!: ../../analogue
        state: 4
            q: * (*напоминание*|*напомин*|*напоминания*) * 
            go!: /Reminder
        script: 
             $reactions.buttons({ text: "Выбрать товар", transition: "/chooseItem" }); 
             $reactions.buttons({ text: "Подобрать аналог", transition: "../analogue" });
             $reactions.buttons({ text: "Поставить напоминание", transition: "../../Reminder" });
             $reactions.buttons({ text: "Что ты умеешь", transition: "/help" });
            

             
#дебаг
    state: errorHttp
        a: Ошибка HTTP-запроса
        script:
            var headers = {
            };
            var result = $http.query("https://tools.aimylogic.com/api/googlesheet2json?id=1srAw0fcdHT19GJV8KR2MbBjDv8Ryq5DZV23JmK6fyoQ", {
                method: "GET",
                headers: headers,
                query: $session,
                dataType: "json",
                timeout: 0 || 10000
            });
            var $httpResponse = result.data;
            $session.httpStatus = result.status;
            client.httpResponse = $httpResponse;
            if (result.isOk && result.status >= 200 && result.status < 300) {
            
                $reactions.transition("/start");
            } else {
                $reactions.transition("/errorHttp");
            };
            
#спрашиваем товар
    state: chooseItem
        a: Какой товар вас интересует или действующее вещество? Может быть клемастин|тавигил или ацетилсалициловая кислота|аспирин?
        buttons:
            "аспирин"
            "тавигил"
            
        state: local
            event: noMatch
            go!: ../../recip
            
        state: 1 
            q: * (*ацетилсалициловая*|*аспирин*|*аспирин*) *             
            go!: ../../no_recip
            
            
#определяем что запрашиваемый товар относится к рецептурным или можно сделать доставку
    state: chooseItemNoRecip
        q: * (*ацетилсалициловая*|*аспирин*|*аспирин*) * 
        script:
            $client.resp = $client.httpResponse[5].mnnname
        go!: ../no_recip

    state: chooseItemRecip
        q!: * (*клемастин*|*тавигил*|*Тавегил*|*Тавигил*) * 
        script:
            $client.resp = $client.httpResponse[0].mnnname 
        go!: ../recip
 
#реакция на нераспознанное
    state: CatchAll  || noContext=true
        event!: noMatch
        random: 
             a: Вы хотите подобрать аналог или узнать о правильном хранении выбранного лекарства?
             a: Вы хотите ппоставить уведомления о приеме лекарств или оформить подписку на автоматическую ежемесячную доставку?  
        script: 
             $reactions.buttons({ text: "Выбрать товар", transition: "/chooseItem" }); 
             $reactions.buttons({ text: "Подобрать аналог", transition: "../analogue" }); 
             $reactions.buttons({ text: "Поставить напоминание", transition: "../Reminder" });
#помощь пользователю             
    state: help  || noContext=true
        q!: * (*помощь*|*что ты умеешь*|*помоги*) * 
        a: Я могу подобрать аналоги по действующуему веществу или бренду, посоветую правильное хранение, могу поставить напоминания звонками о приеме лекарств, или оформить регулярную подписку и доставку на нужный вам товар.
        script: 
             $reactions.buttons({ text: "Выбрать товар", transition: "/chooseItem" }); 
             $reactions.buttons({ text: "Подобрать аналог", transition: "../analogue" }); 
             $reactions.buttons({ text: "Поставить напоминание", transition: "../Reminder" });
        state: 1
            event: noMatch
            go!: /chooseItem 