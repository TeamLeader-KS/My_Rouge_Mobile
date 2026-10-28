package rougelite.BusinessLogicLayer.message

class DisplayingGameMessage {

    /**
     * Приёмник сообщений из бизнес-логики.
     * Логика кладёт сюда строки, UI забирает их после каждого шага.
     * Никакой зависимости от терминала/Android.
     */
    object GameMessageBus {

        private val _messages = mutableListOf<String>()

        /** Добавить сообщение (вызывается из логики). */
        fun post(message: String) {
            if (message.isNotBlank()) {
                _messages.add(message)
            }
        }

        /** Забрать все накопленные сообщения и очистить очередь (вызывается из UI). */
        fun drain(): List<String> {
            val copy = _messages.toList()
            _messages.clear()
            return copy
        }

        /** Очистить без возврата (например, при рестарте игры). */
        fun clear() {
            _messages.clear()
        }
    }
}