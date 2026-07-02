package com.axiom.app.presentation.bodymap;

import com.axiom.app.domain.repository.MuscleGroupRepository;
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
public final class BodyMapViewModel_Factory implements Factory<BodyMapViewModel> {
  private final Provider<MuscleGroupRepository> muscleRepositoryProvider;

  private BodyMapViewModel_Factory(Provider<MuscleGroupRepository> muscleRepositoryProvider) {
    this.muscleRepositoryProvider = muscleRepositoryProvider;
  }

  @Override
  public BodyMapViewModel get() {
    return newInstance(muscleRepositoryProvider.get());
  }

  public static BodyMapViewModel_Factory create(
      Provider<MuscleGroupRepository> muscleRepositoryProvider) {
    return new BodyMapViewModel_Factory(muscleRepositoryProvider);
  }

  public static BodyMapViewModel newInstance(MuscleGroupRepository muscleRepository) {
    return new BodyMapViewModel(muscleRepository);
  }
}
