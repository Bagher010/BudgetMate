package com.example.myapplication.ui.components
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import com.example.myapplication.ui.viewmodel.PendingDelete
import com.example.myapplication.ui.viewmodel.UndoableDelete
@Composable
fun UndoDeleteSnackbarHost(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pendingDelete by viewModel.pendingDelete.collectAsState()
    var lastHandledDeleteId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(pendingDelete) {
        val pending = pendingDelete
        if (pending != null && pending.id != lastHandledDeleteId) {
            lastHandledDeleteId = pending.id
            val message = when (pending.delete) {
                is UndoableDelete.TransactionDelete -> "Transaction deleted"
                is UndoableDelete.SubscriptionDelete -> "Subscription deleted"
                is UndoableDelete.ContactDelete -> "Contact deleted"
                is UndoableDelete.ContactTransactionDelete -> "Transaction deleted"
            }
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    viewModel.undoDelete()
                }
                SnackbarResult.Dismissed -> {
                    viewModel.clearPendingDelete()
                }
            }
        }
    }
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier.padding(bottom = 66.dp)
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            actionColor = MaterialTheme.colorScheme.inversePrimary
        )
    }
}
