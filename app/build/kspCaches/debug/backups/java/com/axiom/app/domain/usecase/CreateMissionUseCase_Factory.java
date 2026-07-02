package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.MissionRepository;
import com.axiom.app.domain.repository.SkillRepository;
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
public final class CreateMissionUseCase_Factory implements Factory<CreateMissionUseCase> {
  private final Provider<MissionRepository> missionRepositoryProvider;

  private final Provider<SkillRepository> skillRepositoryProvider;

  private CreateMissionUseCase_Factory(Provider<MissionRepository> missionRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider) {
    this.missionRepositoryProvider = missionRepositoryProvider;
    this.skillRepositoryProvider = skillRepositoryProvider;
  }

  @Override
  public CreateMissionUseCase get() {
    return newInstance(missionRepositoryProvider.get(), skillRepositoryProvider.get());
  }

  public static CreateMissionUseCase_Factory create(
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider) {
    return new CreateMissionUseCase_Factory(missionRepositoryProvider, skillRepositoryProvider);
  }

  public static CreateMissionUseCase newInstance(MissionRepository missionRepository,
      SkillRepository skillRepository) {
    return new CreateMissionUseCase(missionRepository, skillRepository);
  }
}
