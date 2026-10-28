package rougelite.BusinessLogicLayer.message

/**
 * Замена старого PresentationLayer.ui.Messages.
 * Бизнес-логика по-прежнему вызывает Messages.show(текст, мс), но сама ничего не рисует:
 * сообщение уходит слушателю, которого ставит GameViewModel и который кладёт его в GameUiState.
 */
object Messages {

    @Volatile
    var listener: ((message: String, durationMs: Int) -> Unit)? = null

    @JvmStatic
    fun show(message: String?, durationMs: Int) {
        if (message != null) listener?.invoke(message, durationMs)
    }
}