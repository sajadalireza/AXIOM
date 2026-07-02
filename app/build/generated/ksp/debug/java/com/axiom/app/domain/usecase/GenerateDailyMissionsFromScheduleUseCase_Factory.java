package com.axiom.app.domain.usecase;

import com.axiom.app.data.local.dao.MissionDao;
import com.axiom.app.domain.repository.MissionRepository;
import com.axiom.app.domain.repository.SkillRepository;
import com.axiom.app.domain.repository.WarriorProfileRepository;
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
public final class GenerateDailyMissionsFromScheduleUseCase_Factory implements Factory<GenerateDailyMissionsFromScheduleUseCase> {
  private final Provider<WarriorProfileRepository> warriorRepositoryProvider;

  private final Provider<SkillRepository> skillRepositoryProvider;

  private final Provider<MissionRepository> missionRepositoryProvider;

  private final Provider<MissionDao> missionDaoProvider;

  private GenerateDailyMissionsFromScheduleUseCase_Factory(
      Provider<WarriorProfileRepository> warriorRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider,
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<MissionDao> missionDaoProvider) {
    this.warriorRepositoryProvider = warriorRepositoryProvider;
    this.skillRepositoryProvider = skillRepositoryProvider;
    this.missionRepositoryProvider = missionRepositoryProvider;
    this.missionDaoProvider = missionDaoProvider;
  }

  @Override
  public GenerateDailyMissionsFromScheduleUseCase get() {
    return newInstance(warriorRepositoryProvider.get(), skillRepositoryProvider.get(), missionRepositoryProvider.get(), missionDaoProvider.get());
  }

  public static GenerateDailyMissionsFromScheduleUseCase_Factory create(
      Provider<WarriorProfileRepository> warriorRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider,
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<MissionDao> missionDaoProvider) {
    return new GenerateDailyMissionsFromScheduleUseCase_Factory(warriorRepositoryProvider, skillRepositoryProvider, missionRepositoryProvider, missionDaoProvider);
  }

  public static GenerateDailyMissionsFromScheduleUseCase newInstance(
      WarriorProfileRepository warriorRepository, SkillRepository skillRepository,
      MissionRepository missionRepository, MissionDao missionDao) {
    return new GenerateDailyMissionsFromScheduleUseCase(warriorRepository, skillRepository, missionRepository, missionDao);
  }
}
