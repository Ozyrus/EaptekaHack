require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Дед, ты выпил таблетки?

        state: Yes
            q: * да *
            a: Молодец, дед! Не получишь по жопе.
            script:
                $dialer.hangUp()
            
        state: No
            q: * не* *
            a: Дед, пей таблетки, а то получишь по жопе. Перезвоню и проверю!
            script:
                var now = new Date();
                $dialer.redial({
                    startDateTime: new Date(now.getTime() + 1 * 60000),  // Повторный звонок через минуту
                    finishDateTime: new Date(now.getTime() + 4 * 60000), // В течение 3 минут
                    maxAttempts: 2,                                       // 2 попытки дозвониться
                    retryIntervalInMinutes: 1                             // Пауза между попытками 1 минута
                });
                $dialer.hangUp()
                
    state: NoInput || noContext=true
        event: speechNotRecognized
        script:
            $session.noInputCounter = $session.noInputCounter || 0;
            $session.noInputCounter++;
    
        if: $session.noInputCounter >= 2
            a: Дед, тебя не слышно совсем. Перезвоню.
            script:
                $dialer.redial({
                    startDateTime: new Date(now.getTime() + 1 * 60000),  // Повторный звонок через минуту
                    finishDateTime: new Date(now.getTime() + 4 * 60000), // В течение 3 минут
                    maxAttempts: 2,                                       // 2 попытки дозвониться
                    retryIntervalInMinutes: 1                             // Пауза между попытками 1 минута
                });
                $dialer.hangUp('(Бот повесил трубку)');
        else:
            a: Дед, тебя не слышно! Повтори.