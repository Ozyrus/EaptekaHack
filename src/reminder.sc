theme: /

    state: Reminder
        q!: * (*поставить напоминание*) *
        a: Я могу поставить напоминания о приеме этого лекарства в приложении, или напоминать звонками.
        buttons:
            "В приложении"
            "Звонками"
        
        state: Call
            q: Звонками
            script:
                $session.call = 1
            go!: /SetReminder
            
        state: Push
            q: В приложении
            script:
                $session.call = 0
            go!: /SetReminder
            
    state: SetReminder
        event: новое напоминание
        a: Вижу, что это лекарство нужно принимать неделю, раз в день перед сном.
        a: В какое время вам напомнить о приеме?
        
        state: IsTime
            intent: /время
            script:
                $session.time = $parseTree._time
                $temp.reminderTime = moment($session.time.value).locale("ru").calendar()
            a: Окей. Я напомню о приеме {{ $temp.reminderTime }}
            script:
                if ($session.call == 1) {
                    $reactions.transition("/CreateCall")
                } else {
                    $reactions.transition("/CreatePush")
                }
        state: NotTime
            event!: noMatch
            a: Я не понял. Вы сказали: {{$request.query}}. Нужно сказать время, на которое нужно поставить напоминание.
            
    state: CreateCall
        a: Сначала мне нужен ваш номер телефона.

        state: gotPhone
            intent: /телефон
            script:
                $temp.offsetTime = $session.time.timestamp-10800000
                $temp.DateObject = moment($temp.offseTtime)
                $temp.formattedDate = $temp.DateObject.toISOString()
                $temp.formattedPhone = $parseTree._phone.toString()
                var response = makeCall($temp.formattedDate, $temp.formattedPhone)
                if (response.isOk) {
                    $reactions.answer("Ждите звонка")
                    $reactions.transition("/")
                } else {
                    $reactions.answer(toPrettyString(response))
                    $reactions.answer("Что-то пошло не так, странно. Попробуем заново.")
                    $reactions.transition("/CreateCall")
                }
            
        state: wrongPhone
            event: noMatch
            a: Нужен номер телефона.
            go: CreateCall
        
    state: CreatePush
        script:
            var event = $pushgate.createEvent(
                $session.time.value,
                "reminderEvent"
            );
            $session.reminderId = event.id;
            
    state: Remind
        event!: reminderEvent
        a: Дед, ты выпил таблетки?
        
        state: Yes
            q: * да *
            a: Молодец, дед!
            
        state: No
            q: * не* *
            a: Дед, пей таблетки!
    
    #state: NoMatch
        #event!: noMatch
        #a: Я не понял. Вы сказали: {{$request.query}}
