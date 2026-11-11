package app.prumio.core.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base class of every screen ViewModel (unidirectional data flow, docs/architecture/presentation-pattern.md).
 *
 * - [state]: the single immutable UI state, rendered by the stateless `XxxScreen`.
 * - [onAction]: the only entry point for user intents.
 * - [effects]: one-off events (navigation, snackbars) collected once by the `XxxRoute`.
 */
abstract class PrumioViewModel<State : Any, Action : Any, Effect : Any>(initialState: State) : ViewModel() {
    private val mutableState = MutableStateFlow(initialState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    private val effectChannel = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = effectChannel.receiveAsFlow()

    protected val currentState: State get() = mutableState.value

    abstract fun onAction(action: Action)

    protected fun updateState(reducer: (State) -> State) {
        mutableState.update(reducer)
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { effectChannel.send(effect) }
    }
}
