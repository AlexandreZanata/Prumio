# Recipe: add a use case

A use case is one business operation (`CompleteTaskUseCase`) or one observation
(`ObserveTodayUseCase`). Reference: `core/domain/.../today/GetDayPeriodUseCase.kt`.

## Steps

1. Confirm the rules it enforces exist in [business-rules.md](../../domain/business-rules.md)
   ([add-business-rule.md](add-business-rule.md) if not).
2. Search `core/domain` for an existing use case doing the same; extend instead of duplicating.
3. Create `core/domain/src/commonMain/kotlin/app/prumio/core/domain/<aggregate>/<Verb><Noun>UseCase.kt`:

   ```kotlin
   package app.prumio.core.domain.task

   /**
    * Completes an open task.
    *
    * Rules: BR-TASK-003 (transition), BR-TASK-005 (recurring), BR-MATTER-006 (linked matter).
    */
   class CompleteTaskUseCase(
       private val tasks: TaskRepository,
       private val timeProvider: TimeProvider,
   ) {
       suspend operator fun invoke(id: TaskId): Outcome<Unit, DomainError> {
           val task = tasks.get(id) ?: return DomainError.NotFound("Task", id.value).asFailure()
           if (task.status != TaskStatus.Open) {
               return DomainError.InvalidTransition("BR-TASK-003", task.status.name, "Completed").asFailure()
           }
           // … pure decisions here; persistence through the port
           return Unit.asSuccess()
       }
   }
   ```
   Rules: constructor injection only; exactly one public `operator fun invoke` 🔒; return
   `Outcome<…, DomainError>` for writes that can fail, `Flow<…>` for observations; time from
   `TimeProvider`; ids from `IdGenerator`.
4. Register it in `core/domain/src/commonMain/kotlin/app/prumio/core/domain/di/CoreDomainModule.kt`:
   `factoryOf(::CompleteTaskUseCase)`.
5. Tests in `core/domain/src/commonTest/kotlin/app/prumio/core/domain/<aggregate>/<Name>Test.kt`
   with fakes from `:core:testing`; one test per rule/edge case, named with the rule id.
6. If it needs a new port method, follow [add-repository.md](add-repository.md) and update
   [contracts.md](../../architecture/contracts.md).

## Validation

```bash
./gradlew :core:domain:testAndroidHostTest :app:shell:testAndroidHostTest   # incl. Koin graph
```
