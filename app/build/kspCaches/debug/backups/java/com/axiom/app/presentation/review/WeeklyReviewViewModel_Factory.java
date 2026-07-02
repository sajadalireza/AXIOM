package com.axiom.app.presentation.review;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.data.local.dao.WeeklyReviewDao;
import com.axiom.app.domain.repository.MissionRepository;
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
public final class WeeklyReviewViewModel_Factory implements Factory<WeeklyReviewViewModel> {
  private final Provider<MissionRepository> missionRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<WeeklyReviewDao> weeklyReviewDaoProvider;

  private WeeklyReviewViewModel_Factory(Provider<MissionRepository> missionRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<WeeklyReviewDao> weeklyReviewDaoProvider) {
    this.missionRepositoryProvider = missionRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
    this.weeklyReviewDaoProvider = weeklyReviewDaoProvider;
  }

  @Override
  public WeeklyReviewViewModel get() {
    return newInstance(missionRepositoryProvider.get(), preferencesProvider.get(), weeklyReviewDaoProvider.get());
  }

  public static WeeklyReviewViewModel_Factory create(
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<WeeklyReviewDao> weeklyReviewDaoProvider) {
    return new WeeklyReviewViewModel_Factory(missionRepositoryProvider, preferencesProvider, weeklyReviewDaoProvider);
  }

  public static WeeklyReviewViewModel newInstance(MissionRepository missionRepository,
      AxiomPreferences preferences, WeeklyReviewDao weeklyReviewDao) {
    return new WeeklyReviewViewModel(missionRepository, preferences, weeklyReviewDao);
  }
}
