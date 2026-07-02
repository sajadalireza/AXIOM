package com.axiom.app.presentation.analytics;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.data.local.dao.KPIProgressDao;
import com.axiom.app.domain.repository.DailyHabitLogRepository;
import com.axiom.app.domain.repository.MissionRepository;
import com.axiom.app.domain.repository.MuscleGroupRepository;
import com.axiom.app.domain.repository.VitalsRepository;
import com.axiom.app.domain.repository.WarriorProfileRepository;
import com.axiom.app.presentation.ceremony.CeremonyEngine;
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
public final class WeeklyAnalyticsViewModel_Factory implements Factory<WeeklyAnalyticsViewModel> {
  private final Provider<WarriorProfileRepository> warriorRepositoryProvider;

  private final Provider<MissionRepository> missionRepositoryProvider;

  private final Provider<MuscleGroupRepository> muscleGroupRepositoryProvider;

  private final Provider<VitalsRepository> vitalsRepositoryProvider;

  private final Provider<KPIProgressDao> kpiProgressDaoProvider;

  private final Provider<DailyHabitLogRepository> dailyHabitLogRepositoryProvider;

  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private WeeklyAnalyticsViewModel_Factory(
      Provider<WarriorProfileRepository> warriorRepositoryProvider,
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<MuscleGroupRepository> muscleGroupRepositoryProvider,
      Provider<VitalsRepository> vitalsRepositoryProvider,
      Provider<KPIProgressDao> kpiProgressDaoProvider,
      Provider<DailyHabitLogRepository> dailyHabitLogRepositoryProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.warriorRepositoryProvider = warriorRepositoryProvider;
    this.missionRepositoryProvider = missionRepositoryProvider;
    this.muscleGroupRepositoryProvider = muscleGroupRepositoryProvider;
    this.vitalsRepositoryProvider = vitalsRepositoryProvider;
    this.kpiProgressDaoProvider = kpiProgressDaoProvider;
    this.dailyHabitLogRepositoryProvider = dailyHabitLogRepositoryProvider;
    this.ceremonyEngineProvider = ceremonyEngineProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public WeeklyAnalyticsViewModel get() {
    return newInstance(warriorRepositoryProvider.get(), missionRepositoryProvider.get(), muscleGroupRepositoryProvider.get(), vitalsRepositoryProvider.get(), kpiProgressDaoProvider.get(), dailyHabitLogRepositoryProvider.get(), ceremonyEngineProvider.get(), preferencesProvider.get());
  }

  public static WeeklyAnalyticsViewModel_Factory create(
      Provider<WarriorProfileRepository> warriorRepositoryProvider,
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<MuscleGroupRepository> muscleGroupRepositoryProvider,
      Provider<VitalsRepository> vitalsRepositoryProvider,
      Provider<KPIProgressDao> kpiProgressDaoProvider,
      Provider<DailyHabitLogRepository> dailyHabitLogRepositoryProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new WeeklyAnalyticsViewModel_Factory(warriorRepositoryProvider, missionRepositoryProvider, muscleGroupRepositoryProvider, vitalsRepositoryProvider, kpiProgressDaoProvider, dailyHabitLogRepositoryProvider, ceremonyEngineProvider, preferencesProvider);
  }

  public static WeeklyAnalyticsViewModel newInstance(WarriorProfileRepository warriorRepository,
      MissionRepository missionRepository, MuscleGroupRepository muscleGroupRepository,
      VitalsRepository vitalsRepository, KPIProgressDao kpiProgressDao,
      DailyHabitLogRepository dailyHabitLogRepository, CeremonyEngine ceremonyEngine,
      AxiomPreferences preferences) {
    return new WeeklyAnalyticsViewModel(warriorRepository, missionRepository, muscleGroupRepository, vitalsRepository, kpiProgressDao, dailyHabitLogRepository, ceremonyEngine, preferences);
  }
}
