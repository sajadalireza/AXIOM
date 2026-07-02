package com.axiom.app.data.repository;

import com.axiom.app.data.local.dao.DailyHabitLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DailyHabitLogRepositoryImpl_Factory implements Factory<DailyHabitLogRepositoryImpl> {
  private final Provider<DailyHabitLogDao> dailyHabitLogDaoProvider;

  private DailyHabitLogRepositoryImpl_Factory(Provider<DailyHabitLogDao> dailyHabitLogDaoProvider) {
    this.dailyHabitLogDaoProvider = dailyHabitLogDaoProvider;
  }

  @Override
  public DailyHabitLogRepositoryImpl get() {
    return newInstance(dailyHabitLogDaoProvider.get());
  }

  public static DailyHabitLogRepositoryImpl_Factory create(
      Provider<DailyHabitLogDao> dailyHabitLogDaoProvider) {
    return new DailyHabitLogRepositoryImpl_Factory(dailyHabitLogDaoProvider);
  }

  public static DailyHabitLogRepositoryImpl newInstance(DailyHabitLogDao dailyHabitLogDao) {
    return new DailyHabitLogRepositoryImpl(dailyHabitLogDao);
  }
}
