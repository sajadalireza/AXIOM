package com.axiom.app.presentation.habits;

import com.axiom.app.domain.repository.DailyHabitLogRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DailyCheckinViewModel_Factory implements Factory<DailyCheckinViewModel> {
  private final Provider<DailyHabitLogRepository> repositoryProvider;

  private DailyCheckinViewModel_Factory(Provider<DailyHabitLogRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DailyCheckinViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static DailyCheckinViewModel_Factory create(
      Provider<DailyHabitLogRepository> repositoryProvider) {
    return new DailyCheckinViewModel_Factory(repositoryProvider);
  }

  public static DailyCheckinViewModel newInstance(DailyHabitLogRepository repository) {
    return new DailyCheckinViewModel(repository);
  }
}
