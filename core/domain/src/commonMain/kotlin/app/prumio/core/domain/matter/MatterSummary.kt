package app.prumio.core.domain.matter

import app.prumio.core.model.Matter
import app.prumio.core.model.Task

/** A matter with what the lists need to show about it. */
data class MatterSummary(val matter: Matter, val nextAction: Task?, val needsAttention: Boolean)
